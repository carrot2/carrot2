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
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.getAttributeClass());
        assertTrue(wrapper.getConstraints().isEmpty());
    }

    public void testWithConstraint()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$RangeEditor");
        TypeEditorWrapper wrapper = new TypeEditorWrapper(element);
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.getAttributeClass());
        assertFalse(wrapper.getConstraints().isEmpty());
        assertEquals(1, wrapper.getConstraints().size());
        assertEquals(IntRange.class.getName(), wrapper.getConstraints().get(0));
        assertFalse(wrapper.isAllAtOnce());
    }

    public void testWithConstraints()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$RangeEditor2");
        TypeEditorWrapper wrapper = new TypeEditorWrapper(element);
        assertEquals(EditorsTest.TestType.class.getName(), wrapper.getAttributeClass());
        assertFalse(wrapper.getConstraints().isEmpty());
        assertEquals(2, wrapper.getConstraints().size());
        assertTrue(wrapper.getConstraints().contains(DoubleRange.class.getName()));
        assertTrue(wrapper.getConstraints().contains(ImplementingClasses.class.getName()));
        assertTrue(wrapper.isAllAtOnce());
    }

    private IConfigurationElement findEditor(String editorClassname)
    {
        IConfigurationElement element = null;
        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement editor = elements[i];
            if (editor.getName().equals("typeEditor"))
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
