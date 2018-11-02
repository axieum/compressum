package me.axieum.java.compressum.compress;

import me.axieum.java.compressum.Compressum;
import me.axieum.java.compressum.Constants;
import me.axieum.java.compressum.Format;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ZipTests
{
    final static File ZIP_FILE = new File(Constants.TEMP_DIR, Format.extend("zip_test", Format.ZIP));

    @BeforeAll
    static void init()
    {
        if (ZIP_FILE.exists())
            ZIP_FILE.delete();
    }

    @Test
    @DisplayName("Compress ZIP format")
    void compressZip()
    {
        // Instantiate instance.
        Compressum compressum = new Compressum(ZIP_FILE, Format.ZIP);

        // Add the "/test/resources/sample_data" directory.
        compressum.addEntry(Constants.SAMPLE_DATA_DIR, "Sample Data");

        // Compress and wait - #get() on promise - for archive.
        assertDoesNotThrow(() -> assertNotNull(compressum.compress().get()));

        // Check that the archive exists.
        assertTrue(ZIP_FILE.exists());
    }
}
