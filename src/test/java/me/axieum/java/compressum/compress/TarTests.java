package me.axieum.java.compressum.compress;

import me.axieum.java.compressum.Compressum;
import me.axieum.java.compressum.Constants;
import me.axieum.java.compressum.Format;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TarTests
{
    final static File TAR_FILE = new File(Constants.TEMP_DIR, Format.extend("tar_test", Format.TAR));
    final static File TAR_GZ_FILE = new File(Constants.TEMP_DIR, Format.extend("tar_gz_test", Format.TAR_GZ));
    final static File TAR_XZ_FILE = new File(Constants.TEMP_DIR, Format.extend("tar_xz_test", Format.TAR_XZ));

    @BeforeAll
    static void init()
    {
        if (TAR_FILE.exists())
            TAR_FILE.delete();

        if (TAR_GZ_FILE.exists())
            TAR_GZ_FILE.delete();

        if (TAR_XZ_FILE.exists())
            TAR_XZ_FILE.delete();
    }

    @Test
    @DisplayName("Compress TAR format")
    void compressTar()
    {
        // Instantiate instance.
        Compressum compressum = new Compressum(TAR_FILE, Format.TAR);

        // Add the "/test/resources/sample_data" directory.
        compressum.addEntry(Constants.SAMPLE_DATA_DIR, "Sample Data");

        // Compress!
        assertDoesNotThrow(() -> assertNotNull(compressum.compress().get()));

        // Check that the archive exists.
        assertTrue(TAR_FILE.exists());
    }

    @Test
    @DisplayName("Compress TAR/GZ format")
    void compressTarGZ()
    {
        // Instantiate instance.
        Compressum compressum = new Compressum(TAR_GZ_FILE, Format.TAR_GZ);

        // Add the "/test/resources/sample_data" directory.
        compressum.addEntry(Constants.SAMPLE_DATA_DIR, "Sample Data");

        // Compress!
        assertDoesNotThrow(() -> assertNotNull(compressum.compress().get()));

        // Check that the archive exists.
        assertTrue(TAR_GZ_FILE.exists());
    }

    @Test
    @DisplayName("Compress TAR/XZ format")
    void compressTarXZ()
    {
        // Instantiate instance.
        Compressum compressum = new Compressum(TAR_XZ_FILE, Format.TAR_XZ);

        // Add the "/test/resources/sample_data" directory.
        compressum.addEntry(Constants.SAMPLE_DATA_DIR, "Sample Data");

        // Compress!
        assertDoesNotThrow(() -> assertNotNull(compressum.compress().get()));

        // Check that the archive exists.
        assertTrue(TAR_XZ_FILE.exists());
    }
}
