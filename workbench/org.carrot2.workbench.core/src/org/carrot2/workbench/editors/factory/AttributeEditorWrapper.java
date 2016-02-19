
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

package org.carrot2.workbench.editors.factory;

import static org.carrot2.workbench.core.helpers.ExtensionConfigurationUtils.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.*;

public abstract class AttributeEditorWrapper
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
            WorkbenchCorePlugin.getDefault().getLog().log(
                new OperationStatus(IStatus.ERROR, WorkbenchCorePlugin.PLUGIN_ID, -2,
                    "Error while initializing attribute editor: "
                        + element.getDeclaringExtension().getContributor().getName(), e));
        }

        return null;
    }
}
