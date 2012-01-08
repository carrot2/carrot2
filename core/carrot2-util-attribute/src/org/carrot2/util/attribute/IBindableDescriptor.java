
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.util.Map;
import java.util.Set;


/**
 * Additional, statically derived, metadata describing a {@link Bindable} component.
 */
public interface IBindableDescriptor
{
    /**
     * Returns the attribute prefix used by the component.
     */
    public String getPrefix();

    /**
     * Returns a one sentence summary of the component. It could be presented as a header
     * of the tool tip of the corresponding UI component.
     */
    public String getTitle();

    /**
     * Returns a short label for the component. It can be presented as a label of the
     * corresponding UI component.
     */
    public String getLabel();
    
    /**
     * Returns a one sentence summary of the component. It could be presented as a header of the tool
     * tip of the corresponding UI component.
     */
    public String getDescription();
    
    /**
     * Returns descriptors of attributes declared directly by the component.
     */
    public Set<AttributeInfo> getOwnAttributes();

    /**
     * Returns descriptors of attributes declared by the component or its superclasses.
     */
    public Set<AttributeInfo> getAttributes();

    /**
     * Returns descriptors of attributes declared by the component or its superclasses
     * mapped by the attribute key.
     */
    public Map<String, AttributeInfo> getAttributesByKey();
    
    /**
     * Returns descriptors of attributes declared by the component or its superclasses
     * mapped by the field name.
     */
    public Map<String, AttributeInfo> getAttributesByFieldName();
}
