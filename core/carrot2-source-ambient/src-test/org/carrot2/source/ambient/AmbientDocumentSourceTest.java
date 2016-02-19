
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.ambient;


/**
 * Test cases for {@link AmbientDocumentSource}.
 */
public class AmbientDocumentSourceTest extends
    FubDocumentSourceTestBase<AmbientDocumentSource>
{
    @Override
    public Class<AmbientDocumentSource> getComponentClass()
    {
        return AmbientDocumentSource.class;
    }

    protected FubTestCollection getData()
    {
        return AmbientDocumentSource.DATA;
    }

    protected int getTopicCount()
    {
        return AmbientDocumentSource.TOPIC_COUNT;
    }

    protected Object [] getAllTopics()
    {
        return AmbientDocumentSource.AmbientTopic.values();
    }
}
