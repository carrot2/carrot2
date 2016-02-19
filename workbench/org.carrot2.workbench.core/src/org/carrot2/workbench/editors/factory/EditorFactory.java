
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

import static org.apache.commons.lang.ClassUtils.*;

import java.lang.annotation.Annotation;
import java.util.*;

import org.carrot2.core.IProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.editors.IAttributeEditor;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * See {@link #getEditorFor(Class, AttributeDescriptor)}.
 */
public final class EditorFactory
{
    /*
     * 
     */
    private EditorFactory()
    {
        // no instances.
    }
    
    /**
     * Return the best matching {@link IAttributeEditor} for a given
     * {@link AttributeDescriptor} and {@link IProcessingComponent}.
     * 
     * @param componentClazz Component class or <code>null</code> if no specific component is 
     * available and generic editor should be returned.
     * 
     * @throws EditorNotFoundException If no editor for a given attribute could be found. 
     */
    public static IAttributeEditor getEditorFor(
        Class<? extends IProcessingComponent> componentClazz, AttributeDescriptor attribute)
    {
        IAttributeEditor editor = null;

        if (componentClazz != null)
        {
            editor = findDedicatedEditor(componentClazz, attribute);
        }

        if (editor == null)
        {
            editor = findGenericEditor(attribute);
        }

        if (editor == null)
        {
            throw new EditorNotFoundException("No suitable editor found for attribute: "
                + attribute.toString());
        }

        return editor;
    }

    /**
     * Find a generic attribute editor for a given type.
     */
    private static IAttributeEditor findGenericEditor(AttributeDescriptor attribute)
    {
        List<TypeEditorWrapper> typeCandidates = getCompatibleTypeEditors(attribute);
        if (!typeCandidates.isEmpty())
        {
            typeCandidates = sortTypeEditors(typeCandidates, attribute);
            return typeCandidates.get(0).getExecutableComponent();
        }

        return null;
    }

    /**
     * Find a {@link IProcessingComponent}-dedicated attribute editor.
     */
    private static IAttributeEditor findDedicatedEditor(
        final Class<? extends IProcessingComponent> clazz,
        final AttributeDescriptor attribute)
    {
        List<DedicatedEditorWrapper> candidates =
            getCompatibleDedicatedEditors(clazz, attribute);

        if (!candidates.isEmpty())
        {
            return candidates.get(0).getExecutableComponent();
        }

        return null;
    }

    /**
     * Sort attribute editors based on various criteria (type proximity, number
     * of matching and available constraints).
     */
    private static List<TypeEditorWrapper> sortTypeEditors(
        List<TypeEditorWrapper> editors, final AttributeDescriptor attribute)
    {
        final List<String> annotationNames = Lists.newArrayList();
        for (Annotation ann : attribute.constraints)
        {
            annotationNames.add(ann.annotationType().getName());
        }

        final HashMap<TypeEditorWrapper, Integer> matchingConstraints = Maps.newHashMap();
        for (TypeEditorWrapper t : editors)
        {
            List<String> matches = Lists.newArrayList(annotationNames);
            matches.retainAll(t.constraints);
            matchingConstraints.put(t, matches.size());
        }

        final Comparator<TypeEditorWrapper> comparator = new Comparator<TypeEditorWrapper>()
        {
            public int compare(TypeEditorWrapper o1, TypeEditorWrapper o2)
            {
                int result =
                    distance(attribute.type, o1.attributeClass)
                    - distance(attribute.type, o2.attributeClass);

                /*
                 * Consult the number of matching constraints and pick the more specific
                 * editor (with more available constraints).
                 */
                if (result == 0)
                {
                    result = - (matchingConstraints.get(o1) - matchingConstraints.get(o2));
                }
                
                /*
                 * Consult again in case of a draw and pick the editor that has more optional
                 * constraints (even if they are not present).
                 */
                if (result == 0)
                {
                    result = - (o1.constraints.size() - o2.constraints.size());
                }

                return result;
            }
        };
        
        return Ordering.from(comparator).sortedCopy(editors);
    }

    /**
     * Return a list of {@link TypeEditorWrapper} compatible with an {@link AttributeDescriptor}.
     */
    private static List<TypeEditorWrapper> getCompatibleTypeEditors(
        final AttributeDescriptor attribute)
    {
        final List<String> annotationNames = Lists.newArrayList();
        for (Annotation ann : attribute.constraints)
        {
            annotationNames.add(ann.annotationType().getName());
        }

        return AttributeEditorLoader.INSTANCE
            .filterTypeEditors(new Predicate<TypeEditorWrapper>()
            {
                public boolean apply(TypeEditorWrapper editor)
                {
                    boolean result = isCompatible(attribute.type, editor.attributeClass);

                    /*
                     * For editors with constraints, check allConstraintsRequired condition.
                     */
                    if (result && !editor.constraints.isEmpty() && editor.allConstraintsRequired)
                    {
                        result = annotationNames.containsAll(editor.constraints);
                    }

                    return result;
                }
            });
    }

    /**
     * Return a list of compatible {@link DedicatedEditorWrapper}. There should
     * be zero or at most one.
     */
    private static List<DedicatedEditorWrapper> getCompatibleDedicatedEditors(
        final Class<? extends IProcessingComponent> clazz,
        final AttributeDescriptor attribute)
    {
        return AttributeEditorLoader.INSTANCE
            .filterDedicatedEditors(new Predicate<DedicatedEditorWrapper>()
            {
                public boolean apply(DedicatedEditorWrapper editor)
                {
                    return isCompatible(clazz, editor.componentClass)
                        && editor.attributeId.equals(attribute.key);
                }
            });
    }

    /**
     * Return <code>true</code> if a given <code>className<code> is assignable
     * to <code>clazz</code>.
     */
    @SuppressWarnings("unchecked")
    private static boolean isCompatible(Class<?> clazz, String className)
    {
        /*
         * This checking is currently class-name based instead of using
         * runtime-type information (assignability). 
         * Is this because of class-loader problems?
         */

        boolean compatible = clazz.getName().equals(className);

        if (!compatible)
        {
            List<String> superClasses =
                convertClassesToClassNames(getAllSuperclasses(clazz));
            compatible = superClasses.contains(className);
        }

        if (!compatible && clazz.isInterface())
        {
            compatible = "java.lang.Object".equals(className);
        }

        if (!compatible)
        {
            List<String> interfaces = convertClassesToClassNames(getAllInterfaces(clazz));
            compatible = interfaces.contains(className);
        }

        return compatible;
    }

    /**
     * Return the <i>distance</i> between a given <code>className</code> and
     * <code>clazz</code>. The distance is calculated based on the difference in the
     * number of classes in the hierarchy of inheritance.  
     */
    @SuppressWarnings("unchecked")
    public static int distance(Class<?> clazz, String className)
    {
        if (clazz.getName().equals(className))
        {
            return 0;
        }

        /*
         * Matches everything, but is least specific.
         */
        if (className.equals("java.lang.Object"))
        {
            return Integer.MAX_VALUE;
        }

        int distance = Integer.MAX_VALUE;

        /*
         * Interface match.
         */
        List<String> interfaces = convertClassesToClassNames(getAllInterfaces(clazz));
        if (interfaces.contains(className))
        {
            distance = Math.min(1, distance);
        }

        /*
         * Superclass match.
         */
        List<String> superclasses = convertClassesToClassNames(getAllSuperclasses(clazz));
        if (superclasses.contains(className))
        {
            distance = Math.min(superclasses.indexOf(className) + 1, distance);
        }

        if (distance == Integer.MAX_VALUE)
        {
            throw new RuntimeException("Cannot calculate distance between incompatible classes: "
                + clazz + ", " + className);
        }

        return distance;
    }
}
