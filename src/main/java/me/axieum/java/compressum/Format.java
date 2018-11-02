package me.axieum.java.compressum;

import me.axieum.java.compressum.handler.IArchiveHandler;
import me.axieum.java.compressum.handler.SevenZipHandler;
import me.axieum.java.compressum.handler.TarHandler;
import me.axieum.java.compressum.handler.ZipHandler;

public enum Format
{
    ZIP("zip", new ZipHandler()),
    SEVEN_ZIP("7z", new SevenZipHandler()),
    TAR("tar", new TarHandler()),
    TAR_GZ("tar.gz", new TarHandler()),
    TAR_XZ("tar.xz", new TarHandler());

    private final String extension;
    private final IArchiveHandler handler;

    /**
     * Specifies a new Format enum associated with an extension.
     *
     * @param extension extension name
     */
    Format(String extension, IArchiveHandler handler)
    {
        this.extension = extension;
        this.handler = handler;
    }

    /**
     * Retrieve the extension name string.
     *
     * @return extension name excluding '.'
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Retrieve the class responsible for handling this format.
     *
     * @return the archive handler class
     */
    public IArchiveHandler getHandler()
    {
        return handler;
    }

    /**
     * Retrieves the string representation of the Format.
     *
     * @return extension name excluding '.'
     * @see #getExtension()
     */
    @Override
    public String toString()
    {
        return getExtension();
    }

    /**
     * Append extension(s) to a string.
     *
     * @param base    string prefix
     * @param formats format(s) to be appended to base, delimited by '.'
     * @return base with formats suffixed
     */
    public static String extend(String base, Format... formats)
    {
        StringBuilder sb = new StringBuilder(base);
        for (Format format : formats)
            sb.append('.').append(format.getExtension());
        return sb.toString();
    }
}
