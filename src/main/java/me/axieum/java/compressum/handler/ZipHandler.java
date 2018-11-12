package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.CompletableArchive;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Map;

public class ZipHandler implements IArchiveHandler
{
    @Override
    public File serialize(CompletableArchive completable)
    {
        try
        {
            OutputStream stream = new FileOutputStream(completable.getOutput());
            ArchiveOutputStream archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP,
                                                                                               stream);

            for (Map.Entry<File, String> fileEntry : completable.getEntries().entrySet())
            {
                if (completable.isCancelled())
                    break;

                ZipArchiveEntry entry = new ZipArchiveEntry(fileEntry.getKey(), fileEntry.getValue());
                entry.setSize(fileEntry.getKey().length());

                archive.putArchiveEntry(entry);

                BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileEntry.getKey()));
                IOUtils.copy(input, archive);
                input.close();

                archive.closeArchiveEntry();

                completable.processed++;
            }

            archive.close();

            if (!completable.isCancelled())
                return completable.getOutput().getCanonicalFile();
            completable.getOutput().delete();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
