package me.axieum.java.compressum;

import me.axieum.java.compressum.util.FileNameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Compressum
{
    private HashMap<File, String> entries = new HashMap<>();
    private Format format;
    private File output;

    public Compressum()
    {
        //
    }

    public Compressum(File output, Format format)
    {
        this.output = output;
        this.format = format;
    }

    public Compressum(String output, Format format)
    {
        this(new File(output), format);
    }

    /**
     * Invokes the compression and returns a "promise".
     *
     * @return a "promise" revolving around the archive file instance
     */
    public CompletableFuture<File> compress() throws IOException
    {
        this.validate(); // Do have every necessary?

        return CompletableFuture.supplyAsync(() -> {
            // Prepare the directory for the archive.
            if (!this.getOutput().exists())
                this.getOutput().getParentFile().mkdirs();

            // Archive it!
            File archive = format.getHandler().serialize(this);

            // If the archive file is null or doesn't exist, throw an exception.
            // NB: Throwing an exception allows for catching with #exceptionally()
            if (archive == null || !archive.exists())
                throw new CompletionException(new Exception("Compression of '" + getOutput().getPath() + "' failed!"));

            // Return the archive.
            return archive;
        });
    }

    /**
     * Getters & Setters.
     */

    public HashMap<File, String> getEntries()
    {
        return entries;
    }

    public Format getFormat()
    {
        return format;
    }

    /**
     * Add a new file/directory entry, at the specified path in the archive.
     *
     * @param file   file/directory to be added
     * @param target target pathname in the archive
     * @return this for chaining
     */
    public Compressum addEntry(File file, String target)
    {
        if (target == null || target.isEmpty())
            return addEntry(file);

        if (file.isDirectory())
            for (File descendant : FileUtils.listFiles(file, null, true))
                addEntry(descendant, target + File.separator + FileNameUtils.relativise(file, descendant));
        else
            this.entries.put(file, target);

        return this;
    }

    /**
     * Add a new file/directory entry, preserving its name.
     *
     * @param file file/directory to be added
     * @return this for chaining
     * @see #addEntry(File, String)
     */
    public Compressum addEntry(File file)
    {
        return addEntry(file, file.getName());
    }

    public Compressum setFormat(Format format)
    {
        this.format = format;

        return this;
    }

    public File getOutput()
    {
        return output;
    }

    /**
     * Set the archive's output file.
     *
     * @param output the destination file of the archive
     * @return this for chaining
     */
    public Compressum setOutput(File output)
    {
        this.output = output;

        return this;
    }

    /**
     * Set the archive's output file.
     *
     * @param output the destination pathname of the archive
     * @return this for chaining
     * @see #setOutput(File)
     */
    public Compressum setOutput(String output)
    {
        return setOutput(new File(output));
    }

    /**
     * Validate this Compressum instance ready for execution.
     *
     * @return true if the instance if valid
     * @throws InvalidObjectException     on invalid/no format, or no archive entries
     * @throws IOException                on the output being a directory and not a file
     * @throws FileAlreadyExistsException on the output already existing
     */
    public boolean validate() throws IOException
    {
        // Did they supply us with a format?
        if (format == null)
            throw new InvalidObjectException("Invalid format!");

        // Are there archive entries?
        if (entries.isEmpty())
            throw new InvalidObjectException("There are no file entries to be compressed!");

        // Is the output valid?
        if (output == null)
            throw new InvalidObjectException("Invalid output!");
        if (output.isDirectory())
            throw new IOException("'" + output.getPath() + "' is not an output file!");
        if (output.exists())
            throw new FileAlreadyExistsException(output.getPath());

        return true;
    }
}
