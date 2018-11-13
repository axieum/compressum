package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.CompletableArchive;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class SevenZipHandler implements IArchiveHandler
{
    @Override
    public File serialize(CompletableArchive completable) throws IOException
    {
        SevenZOutputFile archive = new SevenZOutputFile(completable.getOutput());

        for (Map.Entry<File, String> fileEntry : completable.getEntries().entrySet())
        {
            if (completable.isCancelled())
                break;

            SevenZArchiveEntry entry = archive.createArchiveEntry(fileEntry.getKey(), fileEntry.getValue());

            archive.putArchiveEntry(entry);

            InputStream stream = new FileInputStream(fileEntry.getKey());
            final byte[] buffer = new byte[4096];
            int n;
            while (-1 != (n = stream.read(buffer)))
                archive.write(buffer, 0, n);
            stream.close();

            archive.closeArchiveEntry();

            completable.processed++;
        }

        archive.close();

        if (!completable.isCancelled())
            return completable.getOutput().getCanonicalFile();

        completable.getOutput().delete();
        return null;
    }
}
