
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.ant;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.deps.FileReference;


/**
 * An ANT task that prints all the
 * dependencies of a given component. 
 */
public class CopyDependencies extends Task {

    /**
     * A directory where dependencies are to be copied.
     */
    private File toDir;

	/**
     * The component info file of the component
     * for which dependencies are to be retrieved.
     */
    private File componentDescriptor;

    /**
     * A list of <code>FileSet</code> objects
     * that point to component dependency descriptors.
     * This list is used when searching for named
     * dependencies. 
     */
    private LinkedList dependencies = new LinkedList();

    /** Profile for dependency traversal. */
    private String profile;

    /**
     * Set the profile for dependency traversal.
     * @param profile
     */
    public void setProfile(String profile) {
        if (profile != null && "".equals(profile.trim())) {
            profile = null;
        } else
        	this.profile = profile;
    }

    /**
     * Sets the directory where dependencies will be copied.
     * @param file
     */
    public void setToDir(File file) {
        this.toDir = file;
    }
    
    /**
     * Sets a path to the dependency of the component for which dependencies are to be retrieved.
     */
    public void setComponentDescriptor(File dependencyFile) {
        this.componentDescriptor = dependencyFile;
    }

    /**
     * Crates a new path with a set of dependency files to scan.
     */
    public Path createDependencies() {
        Path newPath = new Path(getProject());
        dependencies.add(newPath);
        return newPath;
    }

    /**
     * Checks if all parameters are correct.
     * @throws BuildException
     */
    protected void checkParameters() 
        throws BuildException
    {
        if (getProject() == null) {
            throw new BuildException("Project reference is required.");
        }

        if (this.componentDescriptor == null) {
            throw new BuildException("componentDescriptor attribute is required.");
        }

        if (this.toDir == null)
            throw new BuildException("toDir attribute is required.");
        
        if (!this.toDir.isDirectory()) {
            throw new BuildException("Not a directory or does not exist: "
                + toDir.getAbsolutePath());
        }
    }

    /**
     * Searches for components that are not up to date and updates
     * them if possible.
     */
    public void execute() throws BuildException {
        checkParameters();
        this.dependencies = Utils.convertPathDependencies(getProject(), dependencies);
        
        FileUtils futils = FileUtils.newFileUtils();
        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            HashMap components = new HashMap();
            components.put(component.getName(), component);
            Utils.loadComponentDependencies( futils, getProject(), this.dependencies, components);

            FileReference [] refs 
                = component.getAllProvidedFileReferences(components, profile, true, true);

            for (int i=0;i<refs.length;i++) {
                if (!refs[i].getAbsoluteFile().exists()) {
                    log("WARNING: file does not exist: "
                        + refs[i].getAbsoluteFile() + ", skipping.", Project.MSG_WARN);
                    continue;
                }
                File toFile = futils.resolveFile(toDir, refs[i].getRelative());
                File dir = toFile.getParentFile();

                Mkdir mkdirTask = (Mkdir) getProject().createTask("mkdir");
                mkdirTask.setProject(getProject());
                mkdirTask.setDir(dir);
                mkdirTask.execute();
                
                Copy copyTask = (Copy) getProject().createTask("copy");
                copyTask.setProject(getProject());
                copyTask.setPreserveLastModified(true);
                copyTask.setFile(refs[i].getAbsoluteFile());
                copyTask.setTofile(toFile);
                copyTask.execute();
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
