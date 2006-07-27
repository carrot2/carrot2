
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

package org.carrot2.ant.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.deps.ComponentInProfile;



/**
 * An ANT task that collects all meta information
 * snippets of a given type from dependencies of a given component. 
 */
public class CollectMetaTask extends BaseDependencyPathTask {

	/**
     * The component info file of the component
     * for which dependencies are to be retrieved.
     */
    private File componentDescriptor;

    /** Profile for dependency traversal. */
    private String profile;
    
    /** Property name to be set */
    private String property;
    
    /** Meta information type to collect. */
    private String type;

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
     * Sets the property to be set with collected meta information.
     */
    public void setProperty(String propertyName) {
        this.property = propertyName;
    }

    /**
     * Sets the meta information type to collect.
     */
    public void setType(String type) {
        this.type = type;
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

        if (this.property == null)
            throw new BuildException("property attribute is required.");
        
        if (this.type == null) {
            throw new BuildException("type attribute is required.");
        }
        
        if (this.profile == null) {
            profile = "";
        }
    }

    /**
     * Searches for components that are not up to date and updates
     * them if possible.
     */
    public void execute() throws BuildException {
        checkParameters();
        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            final Map components = super.getAllComponents();
            Utils.addComponentToMap(component, components);

            // final ComponentInProfile self = new ComponentInProfile(component, profile, false);
            final ComponentInProfile [] dependencies = 
                component.getAllRequiredComponentDependencies(components, profile);

            final ArrayList metas = new ArrayList();
            for (int i = 0; i < dependencies.length; i++) {
                final ComponentDependency cd = dependencies[i].component;
                cd.collectMetas(metas, dependencies[i].profile, type);
            }

            final StringBuffer buf = new StringBuffer();
            for (int i = 0; i < metas.size(); i++) {
                buf.append((String) metas.get(i));
            }

            super.getProject().setNewProperty(property, buf.toString());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
