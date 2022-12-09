package org.templater;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class Templater {
    String basepath;

    Templater(String p) {
        basepath = p;
    }

    public void process() throws IOException {
        File base = new File(basepath);

        if (!base.exists()) {
            throw new RuntimeException("Base path does not exist: " + basepath);
        }

        if (!base.isDirectory()) {
            throw new RuntimeException("Base path is not a directory: " + basepath);
        }

        Map<String, String> filesContetnts = FileUtils.readAllFiles(basepath, "");
        Map<String, List<String>> filesDeps = FileUtils.filesToDeps(filesContetnts);

        if (!GraphUtils.allFilesExist(filesDeps)) {
            System.out.println("Dependency graph contains unknown files");
            return;
        }

        if (GraphUtils.isCyclic(filesDeps)) {
            System.out.println("Dependency graph contains cycles");
            return;
        }

        List<String> sortedFiles = GraphUtils.sortGraph(filesDeps);

        System.out.println("");

        for (String file : sortedFiles) {
            System.out.println(file);
            System.out.println(filesContetnts.get(file));
            System.out.println("");
        }
    }

}
