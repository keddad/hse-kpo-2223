package org.templater;

import java.util.*;

final public class GraphUtils {
    private GraphUtils() {
    } // Because my teacher says so

    /**
     * Checks that dependency graph doesn't mention unknown files
     *
     * @param deps A map of file names to a list of files that they require
     */
    public static Boolean allFilesExist(Map<String, List<String>> deps) {
        boolean flag = true;

        for (String file : deps.keySet()) {
            for (String dep : deps.get(file)) {
                if (!deps.containsKey(dep)) {
                    flag = false;

                    System.out.println("Unknown required file: " + dep + " in " + file);
                }
            }
        }

        return flag;
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
                System.out.println("Cycle detected: " + key + " -> " + child);
                return true;
            }
        }

        isCurrentRecpath.put(key, false);

        return false;
    }

    /**
     * Checks that dependency graph doesn't contain cycles
     *
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

    private static List<String> allDeps(String key, Map<String, List<String>> graph) {
        List<String> deps = new ArrayList<>();

        for (String dep : graph.get(key)) {
            if (!deps.contains(dep)) {
                deps.add(dep);

                allDeps(dep, graph).forEach(d -> {
                    if (!deps.contains(d)) {
                        deps.add(d);
                    }
                });
            }
        }

        return deps;
    }

    /**
     * @param graph A map of file names to a list of files that they require
     * @return A list of files where each file is guaranteed to be before all files that it requires
     */
    public static List<String> sortGraph(Map<String, List<String>> graph) {
        Map<String, List<String>> deepDeps = new HashMap<>() {{
            for (String key : graph.keySet()) {
                put(key, allDeps(key, graph));
            }
        }};

        Comparator<String> sortByDeps = (key_l, key_r) -> {

            if (deepDeps.get(key_l).contains(key_r) && !deepDeps.get(key_r).contains(key_l)) {
                return 1;
            }

            if (deepDeps.get(key_r).contains(key_l) && !deepDeps.get(key_l).contains(key_r)) {
                return -1;
            }

            return 0;
        };

        return new ArrayList<>(graph.keySet()).stream().sorted(sortByDeps).toList();
    }
}
