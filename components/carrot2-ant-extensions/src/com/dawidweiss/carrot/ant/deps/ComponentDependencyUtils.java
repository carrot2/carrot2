
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
package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/**
 */
public final class ComponentDependencyUtils {

	private ComponentDependencyUtils() {
		super();
	}

    public static void loadComponentDependencies(FileUtils futils, 
        Project project, List listOfFileSetsWithDependencies, 
        HashMap components)
        throws Exception
    {
        for (Iterator i = listOfFileSetsWithDependencies.iterator(); i.hasNext();) {
            FileSet fs = (FileSet) i.next();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            File fromDir = fs.getDir(project);
            String[] srcFiles = ds.getIncludedFiles();

            for (int j = 0; j < srcFiles.length; j++) {
                ComponentDependency dep = 
                    new ComponentDependency(
                        project, futils.resolveFile(fromDir, srcFiles[j]).getCanonicalFile());

                if (components.containsKey(dep.getName())) {
                    ComponentDependency existingDependency = (ComponentDependency) components.get(dep.getName());
                    if (!existingDependency.getFile().getCanonicalPath().equals(
                        dep.getFile().getCanonicalPath())) {
                        throw new BuildException("Component name duplicated: " +
                            dep.getFile() + " and " +
                            ((ComponentDependency) components.get(dep.getName())).getFile());
                    }
                    // just silently ignore -- it is the same file.
                } else {
                    components.put(dep.getName(), dep);
                }
            }
        }
    }
}
