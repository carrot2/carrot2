
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.odp.mixer;

import java.util.*;

/**
 * Stores references to all {@link org.carrot2.input.odp.mixer.TopicMixer}s
 * available in the framework.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
     * Returns a {@link org.carrot2.input.odp.index.TopicIndex}for given
     * name or <code>null</code> if no topic is associated with given name.
     * 
     * @param name
     */
    public static TopicMixer getTopicMixer(String name)
    {
        return (TopicMixer) topicMixers.get(name);
    }

    /**
     * Returns an iterator of {@link String}s representing the names of all
     * available topic mixers.
     * 
     */
    public static Iterator getAllTopicMixerNames()
    {
        return topicMixers.keySet().iterator();
    }
}