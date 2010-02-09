
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

import org.carrot2.core.IProcessingComponent;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.carrot2.workbench.editors.EditorsTest.*;

public class FactoryTest extends TestCase
{
    private Class<? extends IProcessingComponent> c;
    private BindableDescriptor desc;

    @Override
    protected void setUp() throws Exception
    {
        c = TestComponent.class;
        desc = BindableDescriptorBuilder.buildDescriptor(c.newInstance(), false);
    }

    public void testFindDedicatedEditor()
    {
        IAttributeEditor editor =
            EditorFactory.getEditorFor(c, desc.attributeDescriptors.get("my.specificAttribute"));
        assertNotNull(editor);
        assertEquals(DedicatedEditor.class, editor.getClass());
    }

    public void testEditorFactory()
    {
        checkEditor(RangeEditor.class, "oneConstraintAttribute");
        checkEditor(RangeModuloEditor.class, "twoConstraintsAttribute");
        checkEditor(RangeEditor.class, "simpleAttribute");
        try
        {
            checkEditor(null, "rangeDoubleAttribute");
        }
        catch (EditorNotFoundException e)
        {
            // should happen
        }
    }

    public void testInheritance() throws Exception
    {
        checkEditor(ComboEditor.class, "comboAttribute");
        checkEditor(ComboEditor.class, "editor");
        try
        {
            checkEditor(null, "editor2");
            fail("No editor for field 'editor2' should be found");
        }
        catch (EditorNotFoundException e)
        {
            // should happen
        }
        checkEditor(SubEditor.class, "subAttribute");
        checkEditor(SubEditor.class, "subRangeAttribute");
        checkEditor(InterfaceEditor.class, "interfaceAttribute");
        checkEditor(InterfaceEditor.class, "interfaceRangeAttribute");
    }

    private void checkEditor(Class<?> expectedEditor, String attId)
    {
        IAttributeEditor editor =
            EditorFactory.getEditorFor(c, desc.attributeDescriptors.get(AttributeUtils
                .getKey(TestComponent.class, attId)));
        assertNotNull(editor);
        assertEquals(expectedEditor, editor.getClass());
    }

    public void testDistance()
    {
        assertEquals(0, EditorFactory.distance(SubTestType.class,
            "org.carrot2.workbench.editors.EditorsTest$SubTestType"));
        assertEquals(1, EditorFactory.distance(SubTestType.class,
            "org.carrot2.workbench.editors.EditorsTest$TestType"));
        assertEquals(Integer.MAX_VALUE, EditorFactory.distance(SubTestType.class,
            "java.lang.Object"));
        assertEquals(1, EditorFactory.distance(SubTestType.class,
            "org.carrot2.workbench.editors.EditorsTest$TestInterface"));
    }
}
