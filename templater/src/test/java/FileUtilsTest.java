import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.templater.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileUtilsTest {
    @Test
    public void fileReaderBasic() throws IOException {
        Map<String, String> targetFiles = new HashMap<>() {{
            put("Folder 1/File 1-1", "File 1-1 contents\n" + "require ‘Folder 2/File 2-1’");
            put("Folder 2/File 2-1", "File 2-1 contents");
            put("Folder 2/File 2-2", "require ‘Folder 1/File 1-1’\n" + "require ‘Folder 2/File 2-1’\n" + "File 2-2 contents");
        }};

        Map<String, String> files = FileUtils.readAllFiles("src/test/resources/BasicExample", "");

        assertEquals(targetFiles, files);
    }

    public void fileReaderEmpty() throws IOException {
        Map<String, String> files = FileUtils.readAllFiles("src/test/resources/EmptyExample", "");
        assertTrue(files.isEmpty());
    }
}
