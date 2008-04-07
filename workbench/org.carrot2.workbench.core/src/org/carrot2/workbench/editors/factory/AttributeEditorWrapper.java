package org.carrot2.workbench.editors.factory;

import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ExtensionWrapperBase;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.*;

public abstract class AttributeEditorWrapper extends ExtensionWrapperBase
{
    public static final String ATT_CLASS = "class";

    protected IConfigurationElement element;

    protected AttributeEditorWrapper(IConfigurationElement element)
    {
        this.element = element;
        getAttribute(element, ATT_CLASS);
    }

    public IAttributeEditor getExecutableComponent()
    {
        try
        {
            return (IAttributeEditor) element.createExecutableExtension(ATT_CLASS);
        }
        catch (CoreException e)
        {
            CorePlugin.getDefault().getLog().log(
                new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -2,
                    "Error while initializing attribute editor : "
                        + element.getDeclaringExtension().getContributor().getName(), e));

            // TODO: This is a fall-through and it does look weird from the UI -- nothing
            // happens,
            // but the view does not show. I guess something like an error popup would be
            // more appropriate
            // to display critical errors?
        }
        return null;
    }

}
