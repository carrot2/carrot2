
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
package com.dawidweiss.carrot.ant;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.deps.ComponentDependencyUtils;
import com.dawidweiss.carrot.ant.deps.ComponentInProfile;


/**
 * An ANT task that brings to date all depending
 * components.
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

    /** Profile for dependency traversal. */
    private String profile;
    
    /**
     * If true, only dependencies will be brought up to date. This
     * help to prevent recursion if a project wants to update all
     * dependencies during its own build phase.
     */
    private boolean dependenciesOnly;

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
            ComponentDependencyUtils.loadComponentDependencies( futils, getProject(), this.dependencies, components);

            ComponentInProfile [] dependencies = component
                .getAllRequiredComponentDependencies(components, profile);
            
            ComponentInProfile self = new ComponentInProfile(component, profile);

            // and check/ execute any of the 'provided/build' elements
            // on them.
            for (int i=0;i<dependencies.length;i++) {
                final ComponentInProfile dependency = dependencies[i];

                if (dependenciesOnly && dependency.equals(self)) {
                    continue;
                }

                log("Checking dependency: " + dependency, Project.MSG_INFO);
                dependency.component.bringUpToDate(getProject(), dependency.profile);
            }
        } catch (Throwable e) {
            throw new BuildException(e);
        }
    }
}
