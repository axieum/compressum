package me.axieum.java.compressum;

import me.axieum.java.compressum.util.FileNameUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class Compressum
{
    private HashMap<File, String> entries = new HashMap<>();
    private Format format;
    private File output;

    /**
     * Create a new blank instance to be manually prepared.
     */
    public Compressum()
    {
        //
    }

    /**
     * Create a new instance with the output and format specified.
     *
     * @param output output archive file
     * @param format desired archive format
     */
    public Compressum(File output, Format format)
    {
        this.output = output;
        this.format = format;
    }

    /**
     * Create a new instance with the output and format specified.
     *
     * @param output output archive pathname
     * @param format desired archive format
     */
    public Compressum(String output, Format format)
    {
        this(new File(output), format);
    }

    /**
     * Invoke the compression on the specified executor.
     *
     * @param executor executor for the task to be executed on (allows for thread control)
     * @return a completable archive instance for handling the underlying future
     */
    public CompletableArchive compress(Executor executor)
    {
        final CompletableArchive completable = new CompletableArchive(this);

        return completable.supply(() -> {
            // Check this instance to make sure we have everything.
            validateForCompression();

            // Prepare the directory for the archive.
            if (output.getParent() != null)
                output.getParentFile().mkdirs();

            // Archive it!
            File archive = format.getHandler().serialize(completable);

            // If not cancelled, and the archive file is null or doesn't exist, throw an exception.
            // NB: Throwing an exception allows for catching with #exceptionally()
            if (!completable.isCancelled())
                if (archive == null || !archive.exists())
                    throw new CompletionException(new Exception("Compression of '" + getOutput().getPath() + "' failed!"));

            // Return the archive.
            return archive;
        });
    }

    /**
     * Invokes the compression.
     *
     * @return a completable archive instance for handling the underlying future
     */
    public CompletableArchive compress()
    {
        return compress(null);
    }

    /**
     * Get the desired format of the archive.
     *
     * @return the format to be compressed into
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * Set the desired archive format.
     *
     * @param format Format to be compressed into
     * @return this for chaining
     */
    public Compressum setFormat(Format format)
    {
        this.format = format;

        return this;
    }

    /**
     * Retrieve all archive file entries.
     *
     * @return a map of Files and their pathname in the archive
     */
    public HashMap<File, String> getEntries()
    {
        return entries;
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
            entries.put(file, target);

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

    /**
     * Append an already established file entry map with the entries.
     *
     * @param entries an already established map of files to pathname in the archive
     * @return this for chaining
     */
    public Compressum addEntries(HashMap<File, String> entries)
    {
        this.entries.putAll(entries);
        return this;
    }

    /**
     * Get an instance of a File for the output archive.
     *
     * @return the output archive file
     */
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
     * Validate this Compressum instance to ensure is ready for compression.
     *
     * @throws CompletionException if the instance is missing something
     * @see #compress()
     */
    private void validateForCompression() throws CompletionException
    {
        if (format == null)
            throw new CompletionException(new InvalidObjectException("Invalid format!"));
        if (output == null)
            throw new CompletionException(new InvalidObjectException("Invalid output!"));
        if (output.isDirectory())
            throw new CompletionException(new IOException("'" + output.getPath() + "' is not an output file!"));
        if (output.exists())
            throw new CompletionException(new FileAlreadyExistsException(output.getPath()));
    }
}
