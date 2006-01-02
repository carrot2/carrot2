package com.chilang.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileUtils {

    /**
     * List XML files from given directory
     * @param path
     * @return
     */
    public static File[] listXMLFiles(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        return files;
    }


}
