
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

package org.carrot2.workbench.editors.factory;

import junit.framework.TestCase;

import org.eclipse.core.runtime.*;

public class DedicatedEditorWrapperTest extends TestCase
{
    private IConfigurationElement [] elements;

    @Override
    protected void setUp() throws Exception
    {
        final IExtension [] extensions =
            Platform.getExtensionRegistry().getExtensionPoint(
                "org.carrot2.workbench.core", "attributeEditor").getExtensions();
        IExtension testExtension = null;
        for (int i = 0; i < extensions.length; i++)
        {
            IExtension extension = extensions[i];
            if (extension.getUniqueIdentifier().equals(
                "org.carrot2.workbench.core.test.testEditors"))
            {
                testExtension = extension;
                break;
            }
        }
        elements = testExtension.getConfigurationElements();
    }

    public void testCorrectEditor()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$DedicatedEditor");
        DedicatedEditorWrapper wrapper = new DedicatedEditorWrapper(element);
        assertEquals("my.specificAttribute", wrapper.attributeId);
        assertEquals("org.carrot2.workbench.editors.EditorsTest$TestComponent", wrapper.componentClass);
    }

    private IConfigurationElement findEditor(String editorClassname)
    {
        IConfigurationElement element = null;
        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement editor = elements[i];
            if (editor.getName().equals("dedicated-editor"))
            {
                if (editor.getAttribute(TypeEditorWrapper.ATT_CLASS).equals(
                    editorClassname))
                {
                    element = editor;
                }
            }
        }
        return element;
    }

}
