package org.carrot2.workbench.editors.factory;

import junit.framework.TestCase;

import org.carrot2.util.attribute.*;
import org.carrot2.workbench.editors.EditorNotFoundException;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.carrot2.workbench.editors.EditorsTest.*;

public class FactoryTest extends TestCase
{
    private TestComponent c;
    private BindableDescriptor desc;

    @Override
    protected void setUp() throws Exception
    {
        c = new TestComponent();
        desc = BindableDescriptorBuilder.buildDescriptor(c, false);
    }

    public void testFindDedicatedEditor()
    {
        checkEditor(DedicatedEditor.class, "specificAttribute");
    }

    public void testEditorFactory()
    {
        checkEditor(RangeEditor.class, "rangeAttribute");
        checkEditor(IntEditor.class, "simpleAttribute");
        try
        {
            checkEditor(null, "rangeDoubleAttribute");
            fail("No editor for field 'rangeDoubleAttribute' should be found");
        }
        catch (EditorNotFoundException e)
        {
            // TODO: handle exception
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
            // TODO: handle exception
        }
    }

    private void checkEditor(Class<?> expectedEditor, String attId)
    {
        IAttributeEditor editor =
            EditorFactory.getEditorFor(c, desc.attributeDescriptors.get(AttributeUtils
                .getKey(TestComponent.class, attId)));
        assertNotNull(editor);
        assertEquals(expectedEditor, editor.getClass());
    }

}
