/*
 * CatidTopicMixer.java
 * 
 * Created on 2004-06-28
 */
package com.stachoodev.carrot.odp.mixer;

import java.util.*;

import com.dawidweiss.carrot.util.common.*;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.index.*;

/**
 * Mixes ODP topics based on a list of ODP <code>catid</code>s.
 * 
 * <p>
 * 
 * Query format: the query must be a {@link String}consisting of ODP
 * <code>catid</code> numbers to be mixed separated by spaces, e.g. "234 34534
 * 54246 234".
 * 
 * @author stachoo
 */
public class CatidTopicMixer implements TopicMixer
{
    /** A unique name for this mixer */
    public static final String TOPIC_MIXER_NAME = "catid";

    /**
     * Mixes ODP categories with given <code>catid</code>s.
     * 
     * @param criteria a {@link List}of {@link String}s, each of which is an
     *            ODP <code>catid</code>.
     * @return a {@link List}of {@link Topic}s in the order of input
     *         <code>catid</code>s.
     * @see com.stachoodev.carrot.odp.mixer.TopicMixer#mix(java.lang.Object)
     */
    public List mix(Object criteria)
    {
        if (!(criteria instanceof String))
        {
            throw new IllegalArgumentException(
                "criteria must be an instance of java.lang.String");
        }

        if (!ODPIndex.isInitialized())
        {
            throw new RuntimeException(
                "ODPIndex must be initialized before this method is called");
        }

        PrimaryTopicIndex primaryIndex = ODPIndex.getPrimaryTopicIndex();
        List topics = new ArrayList();

        List catids = StringUtils
            .split((String) criteria, ' ', new ArrayList());

        for (Iterator iter = catids.iterator(); iter.hasNext();)
        {
            String catid = (String) iter.next();

            String location = primaryIndex.getLocation(catid);
            Topic topic = null;
            if (location != null)
            {
                topic = ODPIndex.getTopic(location);
            }

            if (topic != null)
            {
                topics.add(topic);
            }
        }

        return topics;
    }
}