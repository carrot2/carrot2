package org.carrot2.workbench.core.helpers;

import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;

public class VelocityHelper
{
    public static void init()
    {
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        // Disable separate Velocity logging.
        p.setProperty(RuntimeConstants.RUNTIME_LOG, "");

        try
        {
            Velocity.init(p);
        }
        catch (Exception e)
        {
            final IStatus status =
                new OperationStatus(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID, -2,
                    "Error while initiating Velocity engine", e);
            WorkbenchCorePlugin.getDefault().getLog().log(status);
            Utils.showError(status);
        }
    }
}
