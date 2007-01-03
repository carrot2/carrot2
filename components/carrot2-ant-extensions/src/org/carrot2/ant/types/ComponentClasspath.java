
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.tasks.BaseDependencyPathTask;
import org.carrot2.ant.tasks.Utils;



/**
 * A custom datatype extending ant's {@link Path} class and
 * containing all classpath files (JARs and folders) included from 
 * this component dependencies in a given profile.
 */
public class ComponentClasspath extends Path {
    private File componentDescriptor;
    private String profile;  
    private final BaseDependencyPathTask depPathTask;

	public ComponentClasspath(Project project) {
		super(project);
        this.depPathTask = new BaseDependencyPathTask();
        this.depPathTask.setProject(project);
	}

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
        return depPathTask.createDependencies();
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
            throw new BuildException("Could not create component descriptor.", e);
		}

        // load all dependencies pointed to by embedded filesets.
        final Map components;
        try {
            // load all dependencies pointed to by embedded filesets.
            components = this.depPathTask.getAllComponents();
            Utils.addComponentToMap(component, components);
		} catch (Exception e1) {
            throw new BuildException("Could not load dependencies: "
                + e1.toString(), e1);
		}

        File [] providedFiles = component.getAllProvidedFiles(components, profile, true);
        ArrayList result = new ArrayList(providedFiles.length);
        for (int i=0; i < providedFiles.length; i++) {
            getProject().log(providedFiles[i].toString(), Project.MSG_VERBOSE);
            if (!providedFiles[i].isAbsolute()) {
                throw new BuildException("Resolved dependency file not absolute: "
                    + providedFiles[i]);
            }
            // JDK1.5 does not accept files that are not JARs or directories in the
            // classpath.
            if (providedFiles[i].exists() == false) {
                log("Classpath object does not exist: "
                    + providedFiles[i].getAbsolutePath(), Project.MSG_WARN);
            } else {
	            if (providedFiles[i].getName().toLowerCase().endsWith(".jar")) {
		            result.add(providedFiles[i].getAbsolutePath());
	            } else if (providedFiles[i].isDirectory()) {
		            result.add(providedFiles[i].getAbsolutePath());
	            } else {
		            // we don't know what it is, so ignore it.
	                log("Classpath object ignored (not a JAR or a directory): "
	                    + providedFiles[i].getAbsolutePath(), Project.MSG_VERBOSE);
	            }
            }
        }

        return (String []) result.toArray( new String[ result.size() ] );
	}
}
