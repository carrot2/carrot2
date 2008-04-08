package org.carrot2.workbench.editors.factory;

import java.util.List;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.workbench.editors.IAttributeEditor;

import com.google.common.base.Predicate;

public class EditorFactory
{
    public static IAttributeEditor getEditorFor(ProcessingComponent owner,
        AttributeDescriptor attribute)
    {
        IAttributeEditor editor = findDedicatedEditor(owner, attribute);
        return editor;
    }

    private static IAttributeEditor findDedicatedEditor(final ProcessingComponent owner,
        final AttributeDescriptor attribute)
    {
        List<DedicatedEditorWrapper> candidates =
            AttributeEditorLoader.INSTANCE
                .filterDedicatedEditors(new Predicate<DedicatedEditorWrapper>()
                {

                    public boolean apply(DedicatedEditorWrapper editor)
                    {
                        return editor.getComponentClass().equals(
                            owner.getClass().getName())
                            && AttributeUtils.getKey(owner.getClass(),
                                editor.getAttributeId()).equals(attribute.key);
                    }

                });

        if (candidates.isEmpty())
        {
            return null;
        }
        return candidates.get(0).getExecutableComponent();
    }
}
