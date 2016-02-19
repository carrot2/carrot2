
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
 * Test cases for {@link Odp239DocumentSource}.
 */
public class Odp239DocumentSourceTest extends
    FubDocumentSourceTestBase<Odp239DocumentSource>
{
    @Override
    public Class<Odp239DocumentSource> getComponentClass()
    {
        return Odp239DocumentSource.class;
    }

    protected FubTestCollection getData()
    {
        return Odp239DocumentSource.DATA;
    }

    protected int getTopicCount()
    {
        return Odp239DocumentSource.TOPIC_COUNT;
    }

    protected Object [] getAllTopics()
    {
        return Odp239DocumentSource.Odp239Topic.values();
    }
}
