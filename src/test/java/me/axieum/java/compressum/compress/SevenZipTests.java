package me.axieum.java.compressum.compress;

import me.axieum.java.compressum.Compressum;
import me.axieum.java.compressum.Constants;
import me.axieum.java.compressum.Format;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SevenZipTests
{
    final static File SEVEN_ZIP_FILE = new File(Constants.TEMP_DIR, Format.extend("seven_zip_test", Format.SEVEN_ZIP));

    @BeforeAll
    static void init()
    {
        if (SEVEN_ZIP_FILE.exists())
            SEVEN_ZIP_FILE.delete();
    }

    @Test
    @DisplayName("Compress 7z format")
    void compressSevenZip()
    {
        // Instantiate instance.
        Compressum compressum = new Compressum(SEVEN_ZIP_FILE, Format.SEVEN_ZIP);

        // Add the "/test/resources/sample_data" directory.
        compressum.addEntry(Constants.SAMPLE_DATA_DIR, "Sample Data");

        // Compress and wait - #get() on promise - for archive.
        assertDoesNotThrow(() -> assertNotNull(compressum.compress().get()));

        // Check that the archive exists.
        assertTrue(SEVEN_ZIP_FILE.exists());
    }
}
