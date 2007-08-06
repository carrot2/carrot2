
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

package org.carrot2.ant.tasks;

import java.io.File;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Reference;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.deps.ComponentInProfile;



/**
 * An ANT task that brings to date all depending
 * components.
 */
public class BringToDateTask extends BaseDependencyPathTask {

    public static class Override {
        private String refName;
        private String propName;

        Override() {
        }

        public void setProperty(String name) {
            this.propName = name;
        }
        
        public void setReference(String name) {
            this.refName = name;
        }

        public void doOverride(Ant task, Project project) {
            if ((this.propName != null && this.refName != null) ||
                    (this.propName == null && this.refName == null)) {
                throw new BuildException("Either property or reference attribute is required.");
            }
            
            if (propName != null) {
                final String value = project.getProperty(propName);
                // Skip undefined properties
                if (value != null) {
                    final Property property = task.createProperty();
                    property.setName(propName);
                    property.setValue(value);
                }
            } else {
                final Object referencedObject = project.getReference(refName);
                if (referencedObject != null) {
                    final Ant.Reference aref = new Ant.Reference();
                    aref.setRefId(refName);
                    task.addReference(aref);
                }
            }
        }
    }

    /**
     * The component info file of the component
     * for which dependencies are to be retrieved.
     */
    private File componentDescriptor;

    /** Profile for dependency traversal. */
    private String profile;
    
    /**
     * If true, only dependencies will be brought up to date. This
     * helps prevent recursion if a project wants to update all
     * dependencies during its own build phase.
     */
    private boolean dependenciesOnly;

    /**
     * A list of properties to be passed to subprojects.
     */
    private ArrayList overrides = new ArrayList();

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
     * Sets the name of a property to be passed to subprojects
     * if ant build is invoked.
     */
    public Override createOverride() {
        final Override override = new Override();
        this.overrides.add(override);
        return override;
    }

    /**
     * Sets a path to the dependency of the component for which dependencies are to be retrieved.
     */
    public void setComponentDescriptor(File dependencyFile) {
        this.componentDescriptor = dependencyFile;
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
        final Timer timer = new Timer();
        checkParameters();

        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            final Map components = super.getAllComponents();
            Utils.addComponentToMap(component, components);

            final ComponentInProfile [] dependencies = component
                .getAllRequiredComponentDependencies(components, profile);
            
            final ComponentInProfile self = new ComponentInProfile(component, profile, false);

            // and check/ execute any of the 'provided/build' elements
            // on them.
            for (int i = 0; i < dependencies.length; i++) {
                final ComponentInProfile dependency = dependencies[i];

                if (dependenciesOnly && dependency.equals(self)) {
                    continue;
                }

                log("Checking dependency: " + dependency, Project.MSG_INFO);
                dependency.component.bringUpToDate(getProject(), dependency.profile, this);
            }
            log("Updating dependencies of " + component.getName()
                    + " took " + timer.elapsed(), Project.MSG_INFO);
        } catch (Throwable e) {
            throw new BuildException(e);
        }
    }

    /**
     * Returns properties and references to be passed to subant calls. 
     */
    public List getOverrides() {
        return overrides;
    }
}
