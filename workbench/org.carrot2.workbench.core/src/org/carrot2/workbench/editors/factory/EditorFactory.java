package org.carrot2.workbench.editors.factory;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.workbench.editors.EditorNotFoundException;
import org.carrot2.workbench.editors.IAttributeEditor;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class EditorFactory
{
    public static IAttributeEditor getEditorFor(ProcessingComponent owner,
        AttributeDescriptor attribute)
    {
        IAttributeEditor editor = findDedicatedEditor(owner, attribute);
        if (editor == null)
        {
            editor = findTypeEditor(owner, attribute);
        }
        if (editor == null)
        {
            throw new EditorNotFoundException("No suitable editor found for attribute "
                + attribute.toString());
        }
        return editor;
    }

    private static IAttributeEditor findTypeEditor(ProcessingComponent owner,
        AttributeDescriptor attribute)
    {
        List<TypeEditorWrapper> typeCandidates = filterTypeEditors(owner, attribute);
        if (!typeCandidates.isEmpty())
        {
            typeCandidates = sortTypeEditors(typeCandidates, attribute);
            return typeCandidates.get(0).getExecutableComponent();
        }
        return null;
    }

    private static IAttributeEditor findDedicatedEditor(final ProcessingComponent owner,
        final AttributeDescriptor attribute)
    {
        List<DedicatedEditorWrapper> candidates =
            filterDedicatedEditors(owner, attribute);

        if (!candidates.isEmpty())
        {
            return candidates.get(0).getExecutableComponent();
        }
        return null;
    }

    private static List<TypeEditorWrapper> sortTypeEditors(
        List<TypeEditorWrapper> editors, AttributeDescriptor attribute)
    {
        final boolean constraintsPreffered = !attribute.constraints.isEmpty();
        return Lists.sortedCopy(editors, new Comparator<TypeEditorWrapper>()
        {

            public int compare(TypeEditorWrapper o1, TypeEditorWrapper o2)
            {
                if (o1.constraints.isEmpty() && o2.constraints.isEmpty())
                {
                    return 0;
                }
                if (!o2.constraints.isEmpty() && !o2.constraints.isEmpty())
                {
                    return 0;
                }
                if (o1.constraints.isEmpty() ^ constraintsPreffered)
                {
                    return -1;
                }
                return 1;
            }

        });
    }

    private static List<TypeEditorWrapper> filterTypeEditors(
        final ProcessingComponent owner, final AttributeDescriptor attribute)
    {
        return AttributeEditorLoader.INSTANCE
            .filterTypeEditors(new Predicate<TypeEditorWrapper>()
            {

                public boolean apply(TypeEditorWrapper editor)
                {
                    boolean result = isCompatible(attribute.type, editor.attributeClass);
                    if (!editor.constraints.isEmpty())
                    {
                        boolean all = true;
                        boolean one = false;
                        for (String constraintName : editor.constraints)
                        {

                            boolean contains =
                                containsAnnotation(attribute.constraints, constraintName);
                            one |= contains;
                            all &= contains;
                        }
                        if (editor.allAtOnce)
                        {
                            result &= all;
                        }
                        else
                        {
                            result &= one;
                        }
                    }
                    return result;
                }

            });
    }

    private static boolean containsAnnotation(List<Annotation> list, String annotationName)
    {
        for (Annotation annotation : list)
        {
            if (annotation.annotationType().getName().equals(annotationName))
            {
                return true;
            }
        }
        return false;
    }

    private static List<DedicatedEditorWrapper> filterDedicatedEditors(
        final ProcessingComponent owner, final AttributeDescriptor attribute)
    {
        return AttributeEditorLoader.INSTANCE
            .filterDedicatedEditors(new Predicate<DedicatedEditorWrapper>()
            {

                public boolean apply(DedicatedEditorWrapper editor)
                {
                    return isCompatible(owner.getClass(), editor.componentClass)
                        && AttributeUtils.getKey(owner.getClass(), editor.attributeId)
                            .equals(attribute.key);
                }

            });
    }

    private static boolean isCompatible(Class<?> clazz, String className)
    {
        // TODO: should interfaces of component also taken into account?
        boolean compatible = clazz.getName().equals(className);
        Class<?> c = clazz;
        while (!compatible && c.getSuperclass() != null)
        {
            c = c.getSuperclass();
            compatible = c.getName().equals(className);
        }
        if (!compatible && clazz.isInterface())
        {
            compatible = "java.lang.Object".equals(className);
        }
        if (!compatible)
        {
            boolean innerCompatible = false;
            for (int i = 0; i < clazz.getInterfaces().length; i++)
            {
                if (!innerCompatible)
                {
                    Class<?> c2 = clazz.getInterfaces()[i];
                    innerCompatible = isCompatible(c2, className);
                }
            }
            compatible = innerCompatible;
        }
        return compatible;
    }
}
