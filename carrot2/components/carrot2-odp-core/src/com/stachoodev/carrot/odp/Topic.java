/*
 * Topic.java
 * 
 * Created on 2004-06-26
 */
package com.stachoodev.carrot.odp;

import java.util.*;

/**
 * @author stachoo
 */
public interface Topic
{
    /**
     * Returns this MutableTopic's <code>catid</code>.
     * 
     * @return
     */
    public abstract String getCatid();

    /**
     * Returns this MutableTopic's <code>id</code>.
     * 
     * @return
     */
    public abstract String getId();

    /**
     * Returns a list of this MutableTopic's external pages.
     * 
     * @return
     */
    public abstract List getExternalPages();
}