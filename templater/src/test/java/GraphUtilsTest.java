import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.templater.GraphUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphUtilsTest {
    private static final Map<String, List<String>> basicDeps = new HashMap<>() {{
        put("Folder 1/File 1-1", List.of("Folder 2/File 2-1"));
        put("Folder 2/File 2-1", List.of());
        put("Folder 2/File 2-2", List.of("Folder 1/File 1-1", "Folder 2/File 2-1"));
    }};
    @Test
    public void allFilesExistBasic() {
        assertTrue(GraphUtils.allFilesExist(basicDeps));
    }

    @Test
    public void allFilesExistMissing() {
        Map<String, List<String>> deps = new HashMap<>() {{
            put("Folder 1/File 1-1", List.of("Folder 2/File 2-1"));
            put("Folder 2/File 2-1", List.of());
            put("Folder 2/File 2-2", List.of("Folder 1/File 1-1", "Folder 2/File 2-1", "Folder 3/File 3-1"));
        }};
        assertFalse(GraphUtils.allFilesExist(deps));
    }

    @Test
    public void allFilesExistEmpty() {
        Map<String, List<String>> deps = new HashMap<>();
        assertTrue(GraphUtils.allFilesExist(deps));
    }

    @Test
    public void isCyclicBasic() {
        assertFalse(GraphUtils.isCyclic(basicDeps));
    }

    @Test
    public void isCyclicCyclic() {
        Map<String, List<String>> deps = new HashMap<>() {{
            put("Folder 1/File 1-1", List.of("Folder 2/File 2-1"));
            put("Folder 2/File 2-1", List.of("Folder 1/File 1-1"));
            put("Folder 2/File 2-2", List.of("Folder 1/File 1-1", "Folder 2/File 2-1"));
        }};
        assertTrue(GraphUtils.isCyclic(deps));
    }

    @Test
    public void isCyclicLongline() {
        Map<String, List<String>> deps = new HashMap<>() {{
            put("A", List.of("B"));
            put("B", List.of("C"));
            put("C", List.of("A"));
        }};
        assertTrue(GraphUtils.isCyclic(deps));
    }

    @Test
    public void isCyclicEmpty() {
        Map<String, List<String>> deps = new HashMap<>();
        assertFalse(GraphUtils.isCyclic(deps));
    }

    @Test
    public void testSorting() {
        List<String> sorted = GraphUtils.sortGraph(basicDeps);
        assertTrue(sorted.indexOf("Folder 2/File 2-1") < sorted.indexOf("Folder 1/File 1-1"));
        assertTrue(sorted.indexOf("Folder 2/File 2-1") < sorted.indexOf("Folder 2/File 2-2"));
        assertTrue(sorted.indexOf("Folder 1/File 1-1") < sorted.indexOf("Folder 2/File 2-2"));
    }
}
