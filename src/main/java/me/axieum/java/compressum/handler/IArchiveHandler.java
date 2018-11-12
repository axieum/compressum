package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.CompletableArchive;

import java.io.File;

public interface IArchiveHandler
{
    /**
     * Serialise the Compressum instance to disk.
     *
     * @param completable Completable Archive instance that handles the underlying future
     * @return output archive file
     */
    File serialize(CompletableArchive completable);
}
