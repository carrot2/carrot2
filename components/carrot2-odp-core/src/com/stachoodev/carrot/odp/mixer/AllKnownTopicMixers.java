/*
 * AllKnownTopicMixers.java
 * 
 * Created on 2004-06-28
 */
package com.stachoodev.carrot.odp.mixer;

import java.util.*;

/**
 * Stores references to all {@link com.stachoodev.carrot.odp.mixer.TopicMixer}s
 * available in the framework.
 * 
 * @author stachoo
 */
public class AllKnownTopicMixers
{
    /** A mapping between mixer names (keys) and instances (values) */
    private static Map topicMixers;

    /** Initialize the mixers */
    static
    {
        topicMixers = new HashMap();

        topicMixers
            .put(CatidTopicMixer.TOPIC_MIXER_NAME, new CatidTopicMixer());
    }

    /** No instantiation */
    private AllKnownTopicMixers()
    {
    }

    /**
     * Returns a {@link com.stachoodev.carrot.odp.index.TopicIndex}for given
     * name or <code>null</code> if no topic is associated with given name.
     * 
     * @param name
     * @return
     */
    public static TopicMixer getTopicMixer(String name)
    {
        return (TopicMixer) topicMixers.get(name);
    }

    /**
     * Returns an iterator of {@link String}s representing the names of all
     * available topic mixers.
     * 
     * @return
     */
    public static Iterator getAllTopicMixerNames()
    {
        return topicMixers.keySet().iterator();
    }
}