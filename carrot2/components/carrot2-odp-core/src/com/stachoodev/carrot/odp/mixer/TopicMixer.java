/*
 * TopicMixer.java
 * 
 * Created on 2004-06-28
 */
package com.stachoodev.carrot.odp.mixer;

import java.util.*;

/**
 * Mixes a number of topics from ODP based on some criteria.
 * 
 * @author stachoo
 */
public interface TopicMixer
{
    /**
     * Mixes ODP topics based on given criteria. Implementations may cast the
     * <code>criteria</code> parameter to a more specific type.
     * 
     * @param criteria
     * @return a {@link List}of {@link com.stachoodev.carrot.odp.Topic}
     *         instances matching mixing criteria. If no topics match mixing
     *         criteria a non- <code>null</code> empty list must be returned.
     */
    public List mix(Object criteria);
}