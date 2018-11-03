package me.axieum.java.compressum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

class CompressumTests
{
    @Test
    @DisplayName("Prepare a blank Compressum instance")
    void prepareBlankInstance()
    {
        // Instantiate blank instance.
        Compressum compressum = new Compressum();

        // Nothing added yet, should start with no formats added.
        assertDoesNotThrow(() -> compressum.compress().exceptionally(e -> {
            assertTrue(e.getCause() instanceof InvalidObjectException);
            assertEquals("Invalid format!", e.getCause().getMessage());
            return null;
        }).get());

        compressum.setFormat(Format.ZIP);

        // Added a format, now should move to output checks.
        assertDoesNotThrow(() -> compressum.compress().exceptionally(e -> {
            assertTrue(e.getCause() instanceof InvalidObjectException);
            assertEquals("Invalid output!", e.getCause().getMessage());
            return null;
        }).get());

        compressum.setOutput(Constants.SAMPLE_DATA_DIR);

        // Set output to a directory...
        assertDoesNotThrow(() -> compressum.compress().exceptionally(e -> {
            assertTrue(e.getCause() instanceof IOException);
            assertEquals("'" + Constants.SAMPLE_DATA_DIR.getPath() + "' is not an output file!",
                         e.getCause().getMessage());
            return null;
        }).get());

        final File FILE_EXISTENT = new File(Constants.SAMPLE_DATA_DIR, "FileExistsException.zip");
        compressum.setOutput(FILE_EXISTENT);

        // Set output to an already existent file...
        assertDoesNotThrow(() -> compressum.compress().exceptionally(e -> {
            assertTrue(e.getCause() instanceof FileAlreadyExistsException);
            assertEquals(FILE_EXISTENT.getPath(), e.getCause().getMessage());
            return null;
        }).get());

        final File output = new File(Constants.TEMP_DIR, "CompressumTests.zip");
        output.delete();
        compressum.setOutput(output);

        // Added valid output, should pass.
        assertDoesNotThrow(() -> compressum.compress().exceptionally(e -> {
            fail();
            return null;
        }).get());
    }
}
