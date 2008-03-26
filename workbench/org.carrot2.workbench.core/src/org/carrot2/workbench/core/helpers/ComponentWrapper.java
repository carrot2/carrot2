package org.carrot2.workbench.core.helpers;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingComponent;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;

public class ComponentWrapper
{
    private IConfigurationElement element;
    private String caption;
    private String className;
    private ImageDescriptor iconDescriptor;

    public ComponentWrapper(IConfigurationElement element, String captionName,
        String className, String iconAttName)
    {
        this.element = element;
        this.caption = element.getAttribute(captionName);
        this.className = className;
        if (StringUtils.isBlank(caption))
        {
            throw new IllegalArgumentException("Missing " + captionName + " attribute");
        }
        String classAtt = element.getAttribute(className);
        if (classAtt == null || classAtt.length() == 0)
        {
            throw new IllegalArgumentException("Missing " + className + " attribute");
        }
        String iconPath = element.getAttribute(iconAttName);
        if (StringUtils.isBlank(iconPath))
        {
            throw new IllegalArgumentException("Missing " + iconAttName + " attribute");
        }
        iconDescriptor =
            CorePlugin.imageDescriptorFromPlugin(element.getContributor().getName(),
                iconPath);
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

    public ProcessingComponent getExecutableComponent()
    {
        try
        {
            return (ProcessingComponent) element.createExecutableExtension(className);
        }
        catch (CoreException e)
        {
            CorePlugin.getDefault().getLog().log(
                new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -2,
                    "Error while initializing component '" + className + ": "
                        + element.getDeclaringExtension().getContributor().getName(), e));

            // TODO: This is a fall-through and it does look weird from the UI -- nothing
            // happens,
            // but the view does not show. I guess something like an error popup would be
            // more appropriate
            // to display critical errors?
        }
        return null;
    }

    public String getId()
    {
        return element.getDeclaringExtension().getUniqueIdentifier();
    }
}
