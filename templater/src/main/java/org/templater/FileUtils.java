package org.templater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final public class FileUtils {

    private FileUtils() {
    }

    /**
     * @param path   Path to the folder to recursively scan for files
     * @param prefix Prefix to add to the file path
     * @return Map of relative file paths to their contents
     * @throws IOException If there are permissions issues or the path is invalid
     */
    public static Map<String, String> readAllFiles(String path, String prefix) throws IOException {
        File base = new File(path);

        if (!base.exists()) {
            throw new RuntimeException("Base path does not exist: " + path);
        }

        if (!base.isDirectory()) {
            throw new RuntimeException("Base path is not a directory: " + path);
        }

        Map<String, String> files = new HashMap<>();

        for (File file : base.listFiles()) {
            if (file.isDirectory()) {
                files.putAll(readAllFiles(file.getAbsolutePath(), prefix + file.getName() + "/"));
            } else {
                files.put(prefix + file.getName(), Files.readString(file.toPath()));
            }
        }

        return files;
    }

    final private static Pattern requirePattern = Pattern.compile("^require '(.*)'$", Pattern.MULTILINE);

    /**
     * @param fileContents Contents of the file
     * @return List of files specified as "require ‘Folder 2/File 2-1’"
     */
    public static List<String> extractRequires(String fileContents) {
        Matcher matcher = requirePattern.matcher(fileContents);

        return matcher.results().map(m -> m.group(1)).toList();
    }

    /**
     * @param files Map of relative file paths to their contents
     * @return Map of relative file paths to the files they require
     */
    public static Map<String, List<String>> filesToDeps(Map<String, String> files) {
        Map<String, List<String>> deps = new HashMap<>();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            deps.put(entry.getKey(), extractRequires(entry.getValue()));
        }

        return deps;
    }

}
