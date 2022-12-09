package org.templater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {
    /**
     * @param path Path to the folder to recursivly scan for files
     * @param prefix Prefix to add to the file path
     * @return Map of relative file paths to their contents
     * @throws IOException
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
}
