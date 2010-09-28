package org.carrot2.util.attribute;

import java.util.Map;
import java.util.Set;


/**
 * Additional, statically derived, metadata describing a {@link Bindable} type.
 */
public interface IBindableDescriptor
{
    public String getPrefix();

    public String getTitle();

    public String getLabel();
    
    public String getDescription();
    
    public Set<AttributeInfo> getOwnAttributes();
    
    public Set<AttributeInfo> getAttributes();
    
    public Map<String, AttributeInfo> getAttributesByKey();
    
    public Map<String, AttributeInfo> getAttributesByFieldName();
}
