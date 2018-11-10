package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.Compressum;
import me.axieum.java.compressum.Format;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.Map;

public class TarHandler implements IArchiveHandler
{
    @Override
    public File serialize(Compressum compressum)
    {
        try
        {
            OutputStream stream = new FileOutputStream(compressum.getOutput());

            // Apply compression?
            if (compressum.getFormat() == Format.TAR_GZ)
                stream = new GzipCompressorOutputStream(stream);
            else if (compressum.getFormat() == Format.TAR_XZ)
                stream = new XZCompressorOutputStream(stream);

            TarArchiveOutputStream archive = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream(
                    ArchiveStreamFactory.TAR,
                    stream);
            archive.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
            archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

            compressum.processed = 0;
            for (Map.Entry<File, String> fileEntry : compressum.getEntries().entrySet())
            {
                TarArchiveEntry entry = new TarArchiveEntry(fileEntry.getKey(), fileEntry.getValue());

                // Set entry flags
                entry.setSize(fileEntry.getKey().length());

                archive.putArchiveEntry(entry);

                BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileEntry.getKey()));
                IOUtils.copy(input, archive);
                input.close();

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
