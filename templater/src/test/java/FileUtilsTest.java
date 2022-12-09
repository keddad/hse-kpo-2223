import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.templater.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtilsTest {

    static private final Map<String, String> basicFiles = new HashMap<>() {{
        put("Folder 1/File 1-1", "File 1-1 contents\n" + "require 'Folder 2/File 2-1'");
        put("Folder 2/File 2-1", "File 2-1 contents");
        put("Folder 2/File 2-2", "require 'Folder 1/File 1-1'\n" + "require 'Folder 2/File 2-1'\n" + "File 2-2 contents");
    }};
    @Test
    public void fileReaderBasic() throws IOException {
        Map<String, String> files = FileUtils.readAllFiles("src/test/resources/BasicExample", "");

        assertEquals(basicFiles, files);
    }

    @Test
    public void fileReaderEmpty() throws IOException {
        Map<String, String> files = FileUtils.readAllFiles("src/test/resources/EmptyExample", "");
        assertTrue(files.isEmpty());
    }

    @Test
    public void matcherBasic() {
        String fileContents = """
                File 1-1 contents
                require 'Folder 2/File 2-1'
                someotherline
                require 'Folder 2/File 2-2'""";
        assertEquals(FileUtils.extractRequires(fileContents), List.of("Folder 2/File 2-1", "Folder 2/File 2-2"));
    }

    @Test
    public void filesToDepsBasic() {
        Map<String, List<String>> deps = FileUtils.filesToDeps(basicFiles);
        assertEquals(deps, new HashMap<>() {{
            put("Folder 1/File 1-1", List.of("Folder 2/File 2-1"));
            put("Folder 2/File 2-1", List.of());
            put("Folder 2/File 2-2", List.of("Folder 1/File 1-1", "Folder 2/File 2-1"));
        }});
    }
}
