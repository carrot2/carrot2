package org.carrot2.workbench.editors;

/**
 * Provider of attribute-change events, host for {@link IAttributeListener}s.
 */
public interface IAttributeChangeProvider
{
    /*
     * 
     */
    public void addAttributeChangeListener(IAttributeListener listener);
    
    /*
     * 
     */
    public void removeAttributeChangeListener(IAttributeListener listener);
}
