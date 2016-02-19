
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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Sets global system property in ANT's current virtual machine.
 */
public class SetPropertyTask
{
    private Project project;

    /** Property to set. */
    private String propertyName;

    /** Value of the property to set. */
    private String value;

    /*
     * 
     */
    public void setSysproperty(String property)
    {
        this.propertyName = property;
    }

    /*
     * 
     */
    public void setValue(String value)
    {
        this.value = value;
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
            throw new BuildException("Empty required 'sysproperty' attribute.");
        }

        project.log("Setting system property: " + propertyName + " = " + value,
            Project.MSG_VERBOSE);
        System.setProperty(propertyName, value);
    }
}
