
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
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
