package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.CompletableArchive;
import org.apache.commons.compress.archivers.ArchiveException;

import java.io.File;
import java.io.IOException;

public interface IArchiveHandler
{
    /**
     * Serialise the Compressum instance to disk.
     *
     * @param completable Completable Archive instance that handles the underlying future
     * @return output archive file
     * @throws IOException      if the output file could not be written to
     * @throws ArchiveException if the archive output stream could not be instantiated
     */
    File serialize(CompletableArchive completable) throws IOException, ArchiveException;
}
