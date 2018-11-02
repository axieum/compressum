package me.axieum.java.compressum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatTests
{
    @Test
    @DisplayName("Formats are associated with appropriate extension")
    void formatWithExtension()
    {
        assertEquals("7z", Format.SEVEN_ZIP.getExtension()); // 7z
        assertEquals("tar", Format.TAR.getExtension()); // TAR
        assertEquals("zip", Format.ZIP.getExtension()); // ZIP
    }

    @Test
    @DisplayName("String is appended with extensions")
    void appendExtensionsToString()
    {
        assertEquals("test.zip", Format.extend("test", Format.ZIP));
    }
}
