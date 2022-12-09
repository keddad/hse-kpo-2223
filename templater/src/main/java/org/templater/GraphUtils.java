package org.templater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class GraphUtils {
    /**
     * Checks that dependency graph doesn't mention unknown files
     * @param deps A map of file names to a list of files that they require
     */
    public static Boolean allFilesExist(Map<String, List<String>> deps) {
        List<String> allValues = deps.values().stream().flatMap(List::stream).toList();

        return deps.keySet().containsAll(allValues);
    }

    private static Boolean graphDfs(String key, Map<String, Boolean> isVisited, Map<String, Boolean> isCurrentRecpath, Map<String, List<String>> graph) {
        if (isCurrentRecpath.get(key)) {
            return true;
        }

        if (isVisited.get(key)) {
            return false;
        }

        isCurrentRecpath.put(key, true);
        isVisited.put(key, true);

        for (String child : graph.get(key)) {
            if (graphDfs(child, isVisited, isCurrentRecpath, graph)) {
                return true;
            }
        }

        isCurrentRecpath.put(key, false);

        return false;
    }

    /**
     * Checks that dependency graph doesn't contain cycles
     * @param graph A map of file names to a list of files that they require
     * @return true if graph contains cycles, false otherwise
     */
    public static Boolean isCyclic(Map<String, List<String>> graph) {
        Map<String, Boolean> isVisited = new HashMap<>() {{
            for (String key : graph.keySet()) {
                put(key, false);
            }
        }};

        Map<String, Boolean> isCurrentRecpath = new HashMap<>() {{
            for (String key : graph.keySet()) {
                put(key, false);
            }
        }};

        for (String key : graph.keySet()) {
            if (graphDfs(key, isVisited, isCurrentRecpath, graph)) {
                return true;
            }
        }

        return false;
    }
}
