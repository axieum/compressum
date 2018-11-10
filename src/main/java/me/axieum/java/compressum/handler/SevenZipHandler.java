package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.Compressum;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class SevenZipHandler implements IArchiveHandler
{
    @Override
    public File serialize(Compressum compressum)
    {
        try
        {
            SevenZOutputFile archive = new SevenZOutputFile(compressum.getOutput());

            compressum.processed = 0;
            for (Map.Entry<File, String> fileEntry : compressum.getEntries().entrySet())
            {
                SevenZArchiveEntry entry = archive.createArchiveEntry(fileEntry.getKey(), fileEntry.getValue());

                archive.putArchiveEntry(entry);

                InputStream stream = new FileInputStream(fileEntry.getKey());
                final byte[] buffer = new byte[4096];
                int n;
                while (-1 != (n = stream.read(buffer)))
                    archive.write(buffer, 0, n);
                stream.close();

                archive.closeArchiveEntry();
                compressum.processed++;
            }

            archive.close();

            return compressum.getOutput().getCanonicalFile();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
