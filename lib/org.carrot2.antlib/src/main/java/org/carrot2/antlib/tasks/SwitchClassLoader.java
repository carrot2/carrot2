
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

import java.util.ArrayList;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * A task-container which sets the context class loader to a given classloader identifier
 * and resets it upon exit.
 */
public class SwitchClassLoader extends Task implements TaskContainer
{
    /** Optional Vector holding the nested tasks */
    private ArrayList<Task> nestedTasks = new ArrayList<Task>();

    /** Class loader reference. */
    private Reference reference;

    /**
     * Use the reference to locate the loader. If the loader is not found, the specified
     * classpath will be used and registered with the specified name.
     */
    public void setLoaderRef(Reference r)
    {
        this.reference = r;
    }

    /**
     * Add a nested task.
     */
    public void addTask(Task nestedTask)
    {
        nestedTasks.add(nestedTask);
    }

    /**
     * Execute all nestedTasks.
     * 
     * @throws BuildException if one of the nested tasks fails.
     */
    public void execute() throws BuildException
    {
        final Object referenced = reference.getReferencedObject();
        final ClassLoader cl;
        if (referenced instanceof ClassLoader)
        {
            cl = (ClassLoader) referenced;
        }
        else if (referenced instanceof Path)
        {
            cl = getProject().createClassLoader((Path) referenced);
        }
        else
        {
            throw new BuildException("Reference to a path or a classloader expected.");
        }

        final Thread self = Thread.currentThread();
        final ClassLoader previous = self.getContextClassLoader();
        try
        {
            self.setContextClassLoader(cl);
            for (Task task : nestedTasks)
            {
                task.perform();
            }
        }
        finally
        {
            self.setContextClassLoader(previous);
        }
    }
}
