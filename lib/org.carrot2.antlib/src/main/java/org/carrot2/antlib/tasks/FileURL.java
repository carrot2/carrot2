
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.antlib.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Converts a file path to a <code>file:</code> URL.
 */
public class FileURL
{
    private Project project;

    /** Property to set. */
    private String propertyName;

    /** File  reference. */
    private File file;

    /*
     * 
     */
    public void setProperty(String property)
    {
        this.propertyName = property;
    }

    /*
     * 
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /*
     * 
     */
    public void setProject(Project project)
    {
        this.project = project;
    }

    /*
     * 
     */
    public void execute()
    {
        if (propertyName == null || propertyName.length() == 0)
        {
            throw new BuildException("Empty required 'property' attribute.");
        }
        
        if (file == null)
        {
            throw new BuildException("Empty required 'file' attribute.");
        }

        project.setNewProperty(propertyName, file.toURI().toString());
    }
}
