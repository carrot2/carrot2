package org.carrot2.workbench.core.helpers;

import java.util.*;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;

public class VelocityHelper
{
    // TODO: unused?
    private static Map<String, Template> templateCache = new HashMap<String, Template>();

    public static void init()
    {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        try
        {
            Velocity.init(p);
        }
        catch (Exception e)
        {
            final IStatus status =
                new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -2,
                    "Error while initiating Velocity engine", e);
            CorePlugin.getDefault().getLog().log(status);
            Utils.showError(status);
        }
    }
}
