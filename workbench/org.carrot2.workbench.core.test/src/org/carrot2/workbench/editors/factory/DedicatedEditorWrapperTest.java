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
        assertEquals("specificAttribute", wrapper.getAttributeId());
        assertEquals("org.carrot2.workbench.editors.EditorsTest$TestComponent", wrapper
            .getComponentClass());
    }

    public void testNoId()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$DedicatedEditor3");
        try
        {
            new DedicatedEditorWrapper(element);
            fail("Dedicated editor without componentClass attribute was created!");
        }
        catch (IllegalArgumentException ex)
        {
            // should happen
        }
    }

    public void testNoAttributeId()
    {
        IConfigurationElement element =
            findEditor("org.carrot2.workbench.editors.EditorsTest$DedicatedEditor4");
        try
        {
            new DedicatedEditorWrapper(element);
            fail("Dedicated editor without attribute id was created!");
        }
        catch (IllegalArgumentException ex)
        {
            // should happen
        }
    }

    private IConfigurationElement findEditor(String editorClassname)
    {
        IConfigurationElement element = null;
        for (int i = 0; i < elements.length; i++)
        {
            IConfigurationElement editor = elements[i];
            if (editor.getName().equals("dedicatedEditor"))
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
