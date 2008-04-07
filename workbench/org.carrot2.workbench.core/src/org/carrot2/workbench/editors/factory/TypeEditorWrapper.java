package org.carrot2.workbench.editors.factory;

import java.lang.annotation.Annotation;
import java.util.*;

import org.carrot2.util.attribute.constraint.IsConstraint;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class TypeEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_ATTRIBUTE_CLASS = "attributeClass";
    public static final String EL_CONSTRAINTS = "constraints";
    public static final String ATT_ALL_AT_ONCE = "allAtOnce";
    public static final String EL_CONSTRAINT = "constraint";
    public static final String ATT_CONSTRAINT_CLASS = "constraintClass";

    private boolean allAtOnce;
    private Class<?> attributeClass;
    private List<Class<? extends Annotation>> constraints;

    @SuppressWarnings("unchecked")
    protected TypeEditorWrapper(IConfigurationElement element)
    {
        super(element);
        Bundle declaringBundle = Platform.getBundle(element.getContributor().getName());
        String attClassName = getAttribute(element, ATT_ATTRIBUTE_CLASS);
        try
        {
            attributeClass = declaringBundle.loadClass(attClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Class '" + attClassName
                + "' could not be loaded!");
        }
        IConfigurationElement constraintsElement =
            getElement(element, EL_CONSTRAINTS, false);
        if (constraintsElement != null)
        {
            constraints = new ArrayList<Class<? extends Annotation>>();
            allAtOnce =
                getBooleanAttribute(constraintsElement, ATT_ALL_AT_ONCE, false, false);
            IConfigurationElement [] constraintElements =
                getChildren(constraintsElement, EL_CONSTRAINT);
            for (int i = 0; i < constraintElements.length; i++)
            {
                IConfigurationElement constraintElement = constraintElements[i];
                String constraintClassName =
                    getAttribute(constraintElement, ATT_CONSTRAINT_CLASS);
                try
                {
                    // TODO: this will fail if plugin is not loaded! Figure this out!
                    Class<?> constraintClass =
                        declaringBundle.loadClass(constraintClassName);
                    if (!constraintClass.isAnnotation())
                    {
                        throw new IllegalArgumentException("Class '"
                            + constraintClassName + " is not an annotation!");
                    }
                    if (!constraintClass.isAnnotationPresent(IsConstraint.class))
                    {
                        throw new IllegalArgumentException("Class '"
                            + constraintClassName + " is not a constraint annotation!");
                    }
                    constraints.add((Class<? extends Annotation>) constraintClass);

                }
                catch (ClassNotFoundException e)
                {
                    throw new IllegalArgumentException("Class '" + attClassName
                        + "' could not be loaded!");
                }
            }
        }
    }

    public boolean isAllAtOnce()
    {
        return allAtOnce;
    }

    public Class<?> getAttributeClass()
    {
        return attributeClass;
    }

    public List<Class<? extends Annotation>> getConstraints()
    {
        if (constraints == null)
        {
            return null;
        }
        else
        {
            return Collections.unmodifiableList(constraints);
        }
    }

}
