package org.carrot2.workbench.editors;

/**
 * Provider of attribute-change events, host for {@link IAttributeListener}s.
 */
public interface IAttributeEventProvider
{
    /*
     * 
     */
    public void addAttributeListener(IAttributeListener listener);
    
    /*
     * 
     */
    public void removeAttributeListener(IAttributeListener listener);
}
