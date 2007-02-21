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
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.Path;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.tasks.BaseDependencyPathTask;
import org.carrot2.ant.tasks.Utils;

/**
 * A custom task that creates and registers a {@link Path} under a given {@link #id}. The path contains classpath files
 * (JARs and folders) included from this component dependencies in a given profile.
 */
public class ComponentClasspath extends Task
{
    private File componentDescriptor;
    private String profile;
    private final BaseDependencyPathTask depPathTask;
    private String id;

    /**
     *
     */
    public ComponentClasspath()
    {
        this.depPathTask = new BaseDependencyPathTask();
    }

    /**
     * 
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Sets the dependency tracking profile.
     */
    public void setProfile(String profile)
    {
        if (profile != null && "".equals(profile.trim())) this.profile = null;
        else this.profile = profile;
    }

    /**
     * Crates a new path with a set of dependency files to scan.
     */
    public Path createDependencies()
    {
        return depPathTask.createDependencies();
    }

    /**
     * Sets component descriptor file.
     */
    public void setComponentDescriptor(File componentDescriptor)
    {
        this.componentDescriptor = componentDescriptor;
    }

    /**
     * Lists objects in classpath.
     */
    private String [] list() throws BuildException
    {
        if (componentDescriptor == null) throw new BuildException("You must specify componentDescriptor attribute.");

        if (!(componentDescriptor.isFile() && componentDescriptor.canRead()))
        {
            throw new BuildException("Component descriptor not readable: " + componentDescriptor.getAbsolutePath());
        }

        this.depPathTask.setProject(super.getProject());

        ComponentDependency component = null;
        try
        {
            component = new ComponentDependency(getProject(), componentDescriptor);
        }
        catch (Exception e)
        {
            throw new BuildException("Could not create component descriptor.", e);
        }

        // load all dependencies pointed to by embedded filesets.
        final Map components;
        try
        {
            // load all dependencies pointed to by embedded filesets.
            components = this.depPathTask.getAllComponents();
            Utils.addComponentToMap(component, components);
        }
        catch (Exception e)
        {
            throw new BuildException("Could not load dependencies: " + e.toString(), e);
        }

        File [] providedFiles = component.getAllProvidedFiles(components, profile, true);
        ArrayList result = new ArrayList(providedFiles.length);
        for (int i = 0; i < providedFiles.length; i++)
        {
            getProject().log(providedFiles[i].toString(), Project.MSG_VERBOSE);
            if (!providedFiles[i].isAbsolute())
            {
                throw new BuildException("Resolved dependency file not absolute: " + providedFiles[i]);
            }
            // JDK1.5 does not accept files that are not JARs or directories in the
            // classpath.
            if (providedFiles[i].exists() == false)
            {
                log("Classpath object does not exist: " + providedFiles[i].getAbsolutePath(), Project.MSG_WARN);
            }
            else
            {
                if (providedFiles[i].getName().toLowerCase().endsWith(".jar"))
                {
                    result.add(providedFiles[i].getAbsolutePath());
                }
                else if (providedFiles[i].isDirectory())
                {
                    result.add(providedFiles[i].getAbsolutePath());
                }
                else
                {
                    // we don't know what it is, so ignore it.
                    log("Classpath object ignored (not a JAR or a directory): " + providedFiles[i].getAbsolutePath(),
                        Project.MSG_VERBOSE);
                }
            }
        }

        return (String []) result.toArray(new String [result.size()]);
    }

    /**
     * 
     */
    public void execute() throws BuildException
    {
        final String [] files = list();
        final Path path = new Path(getProject());
        for (int i = 0; i < files.length; i++) {
            final Path.PathElement pathElement = path.createPathElement();
            pathElement.setLocation(new File(files[i]));
        }
        getProject().addReference(id, path);
    }
}
