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

import org.apache.tools.ant.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.deps.ComponentDependencyUtils;

import java.io.File;
import java.util.*;
import java.util.LinkedList;


/**
 * An ANT task that brings to date all depending
 * components (if they provide
 */
public class BringToDate extends Task {

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

    /** Dumps a verbose info about dependencies. */
    private boolean verbose;

    /** Profile for dependency traversal. */
    private String profile;
    
    /**
     * If true, only dependencies will be brought up to date. This
     * help to prevent recursion if a project wants to update all
     * dependencies during its own build phase.
     */
    private boolean dependenciesOnly;

    /**
     * Public empty constructor
     */
    public BringToDate() {
    }

    public void setDependenciesOnly(boolean flag) {
        this.dependenciesOnly = flag;
    }

    /**
     * Set the profile for dependency traversal.
     * @param profile
     */
    public void setProfile(String profile) {
    	if (profile != null && "".equals(profile.trim()))
    		this.profile = null;
    	else
        	this.profile = profile;
    }

    /**
     * Sets a path to the dependency of the component for which dependencies are to be retrieved.
     */
    public void setComponentDescriptor(File dependencyFile) {
        this.componentDescriptor = dependencyFile;
    }

    /**
     * Crates a new fileset with a set of dependency files to scan.
     */
    public FileSet createDependencies() {
        FileSet newFileset = new FileSet();
        dependencies.add(newFileset);
        return newFileset;
    }

    /**
     * If true, verbose info is printed during dependency tracking.
     */
    public void setVerbose(boolean b) {
        this.verbose = b;
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
    }

    /**
     * Searches for components that are not up to date and updates
     * them if possible.
     */
    public void execute() throws BuildException {
        
        checkParameters();

        FileUtils futils = FileUtils.newFileUtils();
        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            HashMap components = new HashMap();
            components.put(component.getName(), component);
            ComponentDependencyUtils.loadComponentDependencies( futils, getProject(), this.dependencies, components);

            ComponentDependency [] dependencies = component
                .getAllRequiredComponentDependencies( components, profile );

            // and check/ execute any of the 'provided/build' elements
            // on them.
            for (int i=0;i<dependencies.length;i++) {
                ComponentDependency dependency =
                    dependencies[i];

                if (dependenciesOnly && dependency.equals(component)) {
                    continue;
                }
                log("Checking dependency: " + dependency.getName(), 
                    Project.MSG_INFO);
                dependency.bringUpToDate(getProject(), dependency.getActiveProfile());
            }

        } catch (Throwable e) {
            throw new BuildException(e);
        }
    }

}
