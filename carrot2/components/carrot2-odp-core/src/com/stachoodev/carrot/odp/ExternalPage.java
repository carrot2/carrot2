/*
 * ExternalPage.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp;

/**
 * @author stachoo
 */
public interface ExternalPage
{
    /**
     * Returns this MutableExternalPage's <code>description</code>.
     * 
     * @return
     */
    public abstract String getDescription();

    /**
     * Returns this MutableExternalPage's <code>title</code>.
     * 
     * @return
     */
    public abstract String getTitle();
}