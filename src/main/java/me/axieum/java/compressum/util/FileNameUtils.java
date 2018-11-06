package me.axieum.java.compressum.util;

import java.io.File;
import java.io.IOException;

public class FileNameUtils
{
    /**
     * Rename file path to relative to another.
     *
     * @param source directory relative to
     * @param file   file to relativise the path
     * @return the relative pathname (null if failed)
     */
    public static String relativise(File source, File file)
    {
        try
        {
            return file.getCanonicalPath().substring(source.getCanonicalPath().length() + 1);
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
