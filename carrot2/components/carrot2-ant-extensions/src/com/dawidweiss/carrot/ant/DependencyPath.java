/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
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
     * Crates a new path with a set of dependency files to scan.
     */
    public Path createDependencies() {
        Path newPath = new Path(getProject());
        dependencies.add(newPath);
        return newPath;
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
        
        this.dependencies = Utils.convertPathDependencies(getProject(), dependencies);
        
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
        ArrayList result = new ArrayList(providedFiles.length);
        for (int i=0;i<providedFiles.length;i++) {
            getProject().log(providedFiles[i].toString(), Project.MSG_VERBOSE);
            if (!providedFiles[i].isAbsolute()) {
                throw new BuildException("Resolved dependency file not absolute: "
                    + providedFiles[i]);
            }
            // JDK1.5 does not accept files that are not JARs or directories in the
            // classpath.
            if (providedFiles[i].exists()==false) {
                log("Classpath object does not exist: "
                    + providedFiles[i].getAbsolutePath(), Project.MSG_WARN);
            } else {
	            if (providedFiles[i].getName().toLowerCase().endsWith(".jar")) {
		            result.add( providedFiles[i].getAbsolutePath() );
	            } else if (providedFiles[i].isDirectory()) {
		            result.add( providedFiles[i].getAbsolutePath() );
	            } else {
		            // we don't know what it is, so ignore it.
	                log("Classpath object ignored (not a JAR or a directory): "
	                    + providedFiles[i].getAbsolutePath(), Project.MSG_DEBUG);
	            }
            }
        }
        
        return (String []) result.toArray( new String[ result.size() ] );
	}

}
