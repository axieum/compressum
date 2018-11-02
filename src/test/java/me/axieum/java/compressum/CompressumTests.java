package me.axieum.java.compressum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;

import static org.junit.jupiter.api.Assertions.*;

class CompressumTests
{
    @Test
    @DisplayName("Prepare a blank Compressum instance")
    void prepareBlankInstance()
    {
        Throwable e;

        // Instantiate blank instance.
        Compressum compressum = new Compressum();

        // Nothing added yet, should start with no formats added.
        e = assertThrows(InvalidObjectException.class, compressum::validate);
        assertEquals("Invalid format!", e.getMessage());

        compressum.setFormat(Format.ZIP);

        // Added a format, now should move to file entry check.
        e = assertThrows(InvalidObjectException.class, compressum::validate);
        assertEquals("There are no file entries to be compressed!", e.getMessage());

        compressum.addEntry(Constants.SAMPLE_DATA_DIR);

        // Added an entry, now should move to output checks.
        e = assertThrows(InvalidObjectException.class, compressum::validate);
        assertEquals("Invalid output!", e.getMessage());

        compressum.setOutput(Constants.SAMPLE_DATA_DIR);

        // Set output to a directory...
        e = assertThrows(IOException.class, compressum::validate);
        assertEquals("'" + Constants.SAMPLE_DATA_DIR.getPath() + "' is not an output file!", e.getMessage());

        compressum.setOutput(new File(Constants.SAMPLE_DATA_DIR, "FileExistsException.zip"));

        // Set output to an already existent file...
        e = assertThrows(IOException.class, compressum::validate);
        assertEquals(new File(Constants.SAMPLE_DATA_DIR, "FileExistsException.zip").getPath(), e.getMessage());

        compressum.setOutput(new File(Constants.TEMP_DIR, "sample_data.zip"));

        // Added valid output, should pass.
        assertDoesNotThrow(compressum::validate);
    }
}
