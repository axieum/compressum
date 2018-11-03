package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.Compressum;

import java.io.File;

public interface IArchiveHandler
{
    /**
     * Serialise the Compressum instance to disk.
     *
     * @param compressum Compressum instance that contains format, output and archive entries
     * @return output archive file
     */
    File serialize(Compressum compressum);
}
