
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

import org.carrot2.util.attribute.constraint.*;
import org.carrot2.workbench.editors.EditorsTest;
import org.eclipse.core.runtime.*;

public class TypeEditorWrapperTest extends TestCase
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

    public void testNoConstraints()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$IntEditor");
        TypeEditorWrapper wrapper = new TypeEditorWrapper(element);
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.attributeClass);
        assertTrue(wrapper.constraints.isEmpty());
    }

    public void testWithConstraint()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$RangeEditor");
        TypeEditorWrapper wrapper = new TypeEditorWrapper(element);
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.attributeClass);
        assertFalse(wrapper.constraints.isEmpty());
        assertEquals(1, wrapper.constraints.size());
        assertEquals(IntRange.class.getName(), wrapper.constraints.get(0));
        assertFalse(wrapper.allConstraintsRequired);
    }

    public void testWithConstraints()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$RangeEditor2");
        TypeEditorWrapper wrapper = new TypeEditorWrapper(element);
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.attributeClass);
        assertFalse(wrapper.constraints.isEmpty());
        assertEquals(2, wrapper.constraints.size());
        assertTrue(wrapper.constraints.contains(DoubleRange.class.getName()));
        assertTrue(wrapper.constraints.contains(ImplementingClasses.class.getName()));
        assertTrue(wrapper.allConstraintsRequired);
    }

    private IConfigurationElement findEditor(String editorClassname)
    {
        IConfigurationElement element = null;
        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement editor = elements[i];
            if (editor.getName().equals("type-editor"))
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
