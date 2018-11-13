package me.axieum.java.compressum.handler;

import me.axieum.java.compressum.CompletableArchive;
import me.axieum.java.compressum.Format;
import org.apache.commons.compress.archivers.ArchiveException;
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
    public File serialize(CompletableArchive completable) throws IOException, ArchiveException
    {
        OutputStream stream = new FileOutputStream(completable.getOutput());

        // Apply compression?
        if (completable.getFormat() == Format.TAR_GZ)
            stream = new GzipCompressorOutputStream(stream);
        else if (completable.getFormat() == Format.TAR_XZ)
            stream = new XZCompressorOutputStream(stream);

        TarArchiveOutputStream archive = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream(
                ArchiveStreamFactory.TAR,
                stream);
        archive.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        archive.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        for (Map.Entry<File, String> fileEntry : completable.getEntries().entrySet())
        {
            if (completable.isCancelled())
                break;

            TarArchiveEntry entry = new TarArchiveEntry(fileEntry.getKey(), fileEntry.getValue());

            // Set entry flags
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
        return null;
    }
}
