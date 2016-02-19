
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

import static org.carrot2.workbench.core.helpers.ExtensionConfigurationUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import org.carrot2.shaded.guava.common.collect.ImmutableList;

public class TypeEditorWrapper extends AttributeEditorWrapper
{
    public static final String ATT_ATTRIBUTE_CLASS = "attribute-class";
    public static final String EL_CONSTRAINTS = "constraints";
    public static final String ATT_ALL_CONSTRAINTS_REQUIRED = "all-constraints-required";
    public static final String EL_CONSTRAINT = "constraint";
    public static final String ATT_CONSTRAINT_CLASS = "constraint-class";

    public final boolean allConstraintsRequired;
    public final String attributeClass;
    public final List<String> constraints;

    public TypeEditorWrapper(IConfigurationElement element)
    {
        super(element);

        attributeClass = getAttribute(element, ATT_ATTRIBUTE_CLASS);
        IConfigurationElement constraintsElement =
            getElement(element, EL_CONSTRAINTS, false);
        List<String> tempConstraints = new ArrayList<String>();
        if (constraintsElement != null)
        {
            allConstraintsRequired =
                getBooleanAttribute(constraintsElement, ATT_ALL_CONSTRAINTS_REQUIRED, false, false);
            IConfigurationElement [] constraintElements =
                getChildren(constraintsElement, EL_CONSTRAINT);
            for (int i = 0; i < constraintElements.length; i++)
            {
                IConfigurationElement constraintElement = constraintElements[i];
                String constraintClassName =
                    getAttribute(constraintElement, ATT_CONSTRAINT_CLASS);
                tempConstraints.add(constraintClassName);
            }
            constraints = ImmutableList.copyOf(tempConstraints);
        }
        else
        {
            allConstraintsRequired = false;
            constraints = ImmutableList.of();
        }
    }

}
