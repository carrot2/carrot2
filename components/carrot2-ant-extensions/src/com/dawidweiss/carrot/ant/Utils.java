package com.dawidweiss.carrot.ant;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

/**
 * Utility methods that didn't fit anywhere else.
 *  
 * @author Dawid Weiss
 */
public class Utils {

    /**
     * Converts all Path objects that point to directories to 
     * filesets including "*.dep.xml". 
     */
    public static LinkedList convertPathDependencies(Project prj, LinkedList deps) {
        LinkedList output = new LinkedList();
        FileUtils futils = FileUtils.newFileUtils();
        for (Iterator i = deps.iterator(); i.hasNext(); ) {
            Object x = (Object) i.next();
            if (x instanceof FileSet) {
                output.add(x);
            } else if (x instanceof Path) {
                Path p = (Path) x;
                String [] lst = p.list();
                for (int j=0;j<lst.length;j++) {
                    File f = new File( lst[j] );
                    if (!f.exists()) { 
                        prj.log("File does not exist and was removed from path: "
                                + f.getAbsolutePath(), Project.MSG_VERBOSE);
                        continue;
                    }
                    if (f.isFile()) {
                        // ok, it is a file. Add it immediately.
                        FileSet fs = new FileSet();
                        fs.setProject(prj);
                        fs.setDir(f.getParentFile());
                        fs.setIncludes(futils.removeLeadingPath(f.getParentFile(), f));
                        output.add(fs);
                    } else if (f.isDirectory()) {
                        // add a fileset to include all dependencies
                        FileSet fs = new FileSet();
                        fs.setProject(prj);
                        fs.setDir(f);
                        fs.setIncludes("**/*.dep.xml");
                        fs.setIncludes("*.dep.xml");
                        output.add(fs);
                    } else {
                        prj.log("Unknown file type: "
                                + f.getAbsolutePath(), Project.MSG_VERBOSE);
                        continue;
                    }
                }
            } else throw new BuildException("Unknown part of path: "
                    + x.getClass().getName());
        }
        return output;
    }
    
}
