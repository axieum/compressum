package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.Compressum;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Map;

public class ZipHandler implements IArchiveHandler
{
    @Override
    public File serialize(Compressum compressum)
    {
        try
        {
            OutputStream stream = new FileOutputStream(compressum.getOutput());
            ArchiveOutputStream archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP,
                                                                                               stream);

            for (Map.Entry<File, String> fileEntry : compressum.getEntries().entrySet())
            {
                ZipArchiveEntry entry = new ZipArchiveEntry(fileEntry.getValue());
                entry.setSize(fileEntry.getKey().length());
                
                archive.putArchiveEntry(entry);

                BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileEntry.getKey()));
                IOUtils.copy(input, archive);
                input.close();

                archive.closeArchiveEntry();
            }

            archive.finish();
            stream.close();

            return compressum.getOutput().getCanonicalFile();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
