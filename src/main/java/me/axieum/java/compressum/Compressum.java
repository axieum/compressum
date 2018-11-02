package me.axieum.java.compressum;

import me.axieum.java.compressum.util.FileNameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class Compressum
{
    private HashMap<File, String> entries = new HashMap<>();
    private Format[] formats;
    private File output;

    public Compressum()
    {
        //
    }

    public Compressum(File output, Format... formats)
    {
        this.output = output;
        this.formats = formats;
    }

    public Compressum(String output, Format... formats)
    {
        this(new File(output), formats);
    }

    /**
     * Invokes the compression and returns a "promise".
     *
     * @return a "promise" revolving around the archive file instance
     */
    public CompletableFuture<File> compress() throws IOException
    {
        // Validate the instance.
        this.validate();

        // Compress!
        for (Format format : formats)
        {
            return CompletableFuture.supplyAsync(() -> {
                if (!this.getOutput().exists())
                    this.getOutput().getParentFile().mkdirs();
                return format.getHandler().serialize(this);
            });
        }

        return null; // If we reached here, it failed for some reason...
    }

    /**
     * Getters & Setters.
     */

    public HashMap<File, String> getEntries()
    {
        return entries;
    }

    public Format[] getFormats()
    {
        return formats;
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

    public Compressum setFormat(Format... formats)
    {
        this.formats = formats;

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
        if (formats == null || formats.length < 1)
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
