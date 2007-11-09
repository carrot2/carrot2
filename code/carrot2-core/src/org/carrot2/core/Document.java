package org.carrot2.core;

public interface Document
{
    /**
     * Unique identifier of this document.
     */
    Object getId();
    
    /**
     * @see DocumentSource#getFields()
     */
    Object getField(Field f);
}
