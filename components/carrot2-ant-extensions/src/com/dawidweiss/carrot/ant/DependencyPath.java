/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.ant;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.deps.ComponentDependencyUtils;


/**
 * An ANT task that instantiates ANT's <code>Path</code>
 * object and points at the dependencies of a given
 * component.
 */
public class DependencyPath extends Path {

    private File componentDescriptor;
    private String profile;  

	public DependencyPath(Project project) {
		super(project);
	}

    /**
     * A list of <code>FileSet</code> objects
     * that point to component dependency descriptors.
     * This list is used when searching for named
     * dependencies. 
     */
    private LinkedList dependencies = new LinkedList();


    /**
     * Sets the dependency tracking profile.
     */
    public void setProfile(String profile) {
        if (profile != null && "".equals( profile.trim()))
            this.profile = null;
        else 
            this.profile = profile;
    }

    /**
     * Crates a new fileset with a set of dependency
     * files to scan.
     */
    public FileSet createDependencies() {
        FileSet newFileset = new FileSet();
        dependencies.add(newFileset);
        return newFileset;
    }

    /**
     * Sets component descriptor file.
     */
    public void setComponentDescriptor(File componentDescriptor) {
        this.componentDescriptor = componentDescriptor;
    }

    /**
     * Lists objects in classpath.
     */
	public String[] list() throws BuildException {

        if (componentDescriptor == null)
            throw new BuildException("You must specify componentDescriptor attribute.");

        if (!(componentDescriptor.isFile() && componentDescriptor.canRead())) {
            throw new BuildException("Component descriptor not readable: "
                + componentDescriptor.getAbsolutePath());
        }

        ComponentDependency component = null;
        try {
            component = 
			    new ComponentDependency(getProject(), componentDescriptor);
		} catch (Exception e) {
            throw new BuildException("Could not create Component Dependency.", e);
		}
        
        // load all dependencies pointed to by embedded filesets.
        HashMap components = new HashMap();
        components.put(component.getName(), component);
        FileUtils futils = FileUtils.newFileUtils();
        try {
			ComponentDependencyUtils.loadComponentDependencies( futils, getProject(), this.dependencies, components);
		} catch (Exception e1) {
            throw new BuildException("Could not load dependencies: "
                + e1.toString(), e1);
		}

        File [] providedFiles = component.getAllProvidedFiles( components, profile, true );
        String [] result = new String [ providedFiles.length ];
        for (int i=0;i<providedFiles.length;i++) {
            log(providedFiles[i].toString(), Project.MSG_VERBOSE);
            if (!providedFiles[i].isAbsolute()) {
                throw new BuildException("Resolved dependency file not absolute: "
                    + providedFiles[i]);
            }
            if (providedFiles[i].getName().endsWith(".jar")
                && !providedFiles[i].exists()) {
                log("Classpath object does not exist: "
                    + providedFiles[i].getAbsolutePath(), Project.MSG_WARN);
            }
            result[i] = providedFiles[i].getAbsolutePath();
        }
        return result;
	}

}
