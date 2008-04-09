package org.carrot2.workbench.editors.factory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import com.google.common.collect.Lists;

public class TypeEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_ATTRIBUTE_CLASS = "attributeClass";
    public static final String EL_CONSTRAINTS = "constraints";
    public static final String ATT_ALL_AT_ONCE = "allAtOnce";
    public static final String EL_CONSTRAINT = "constraint";
    public static final String ATT_CONSTRAINT_CLASS = "constraintClass";

    public final boolean allAtOnce;
    public final String attributeClass;
    public final List<String> constraints;

    @SuppressWarnings("unchecked")
    public TypeEditorWrapper(IConfigurationElement element)
    {
        super(element);
        attributeClass = getAttribute(element, ATT_ATTRIBUTE_CLASS);
        IConfigurationElement constraintsElement =
            getElement(element, EL_CONSTRAINTS, false);
        List<String> tempConstraints = new ArrayList<String>();
        if (constraintsElement != null)
        {
            allAtOnce =
                getBooleanAttribute(constraintsElement, ATT_ALL_AT_ONCE, false, false);
            IConfigurationElement [] constraintElements =
                getChildren(constraintsElement, EL_CONSTRAINT);
            for (int i = 0; i < constraintElements.length; i++)
            {
                IConfigurationElement constraintElement = constraintElements[i];
                String constraintClassName =
                    getAttribute(constraintElement, ATT_CONSTRAINT_CLASS);
                tempConstraints.add(constraintClassName);
            }
            constraints = Lists.immutableList(tempConstraints);
        }
        else
        {
            allAtOnce = false;
            constraints = Lists.immutableList();
        }
    }

}
