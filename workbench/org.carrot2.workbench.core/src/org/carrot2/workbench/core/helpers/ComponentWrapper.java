package org.carrot2.workbench.core.helpers;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;

public class ComponentWrapper extends ExtensionWrapperBase
{
    private IConfigurationElement element;
    private String caption;
    private String className;
    private ImageDescriptor iconDescriptor;
    private Class<? extends ProcessingComponent> clazz;

    public ComponentWrapper(IConfigurationElement element, String captionName,
        String className, String iconAttName)
    {
        this.element = element;
        this.caption = getAttribute(element, captionName);
        this.className = className;
        getAttribute(element, className);
        String iconPath = getAttribute(element, iconAttName);
        iconDescriptor = WorkbenchCorePlugin.imageDescriptorFromPlugin(element
            .getContributor().getName(), iconPath);
        if (iconDescriptor == null)
        {
            throw new IllegalArgumentException("Resource " + iconPath + " in plugin "
                + element.getContributor().getName()
                + " is not a correct image or does not exist");
        }
    }

    public String getCaption()
    {
        return caption;
    }

    public ImageDescriptor getIcon()
    {
        return iconDescriptor;
    }

    public synchronized Class<? extends ProcessingComponent> getComponentClass()
    {
        if (clazz == null)
        {
            clazz = getExecutableComponent().getClass();
        }
        return clazz;
    }

    public ProcessingComponent getExecutableComponent()
    {
        try
        {
            return (ProcessingComponent) element.createExecutableExtension(className);
        }
        catch (CoreException e)
        {
            WorkbenchCorePlugin.getDefault().getLog().log(
                new OperationStatus(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID, -2,
                    "Error while initializing component '" + className + ": "
                        + element.getDeclaringExtension().getContributor().getName(), e));

            /*
             * TODO: This is a fall-through and it does look weird from the UI -- nothing
             * happens, but the view does not show. I guess something like an error popup
             * would be more appropriate to display critical errors?
             */
        }
        return null;
    }

    public String getId()
    {
        return element.getDeclaringExtension().getUniqueIdentifier();
    }
}
