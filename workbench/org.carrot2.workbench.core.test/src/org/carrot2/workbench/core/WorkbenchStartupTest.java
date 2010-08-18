/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.carrot2.core.ProcessingComponentDescriptor;

public class WorkbenchStartupTest extends TestCase
{
    public void testFailedStartupComponents() throws InterruptedException
    {
        List<ProcessingComponentDescriptor> failed = WorkbenchCorePlugin.getDefault()
            .getFailed();
        if (failed.size() > 0)
        {
            StringBuilder message = new StringBuilder("Startup component failures: ");
            for (ProcessingComponentDescriptor pcd : failed)
            {
                message.append("\n\t")
                    .append(pcd.getId())
                    .append(" ")
                    .append(pcd.getTitle())
                    .append(": ")
                    .append(pcd.getInitializationFailure());
            }
            Assert.fail(message.toString());
        }
    }
}
