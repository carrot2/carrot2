/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.opensearch;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.resource.ResourceUtils;

/**
 * Test cases for {@link OpenSearchDocumentSource} with feeds where start result index is
 * specified.
 */
public class OpenSearchDocumentSourceByPageIncrementTest extends
    QueryableDocumentSourceTestBase<OpenSearchDocumentSource>
{
    @Override
    public Class<OpenSearchDocumentSource> getComponentClass()
    {
        return OpenSearchDocumentSource.class;
    }

    @Override
    protected int getLargeQuerySize()
    {
        return 120;
    }

    @Override
    protected int getSmallQuerySize()
    {
        return 30;
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    public void prepareComponent()
    {
        super.prepareComponent();

        try
        {
            initAttributes.putAll(AttributeValueSets
                .deserialize(
                    ResourceUtils.prefetch(OpenSearchDocumentSourceByPageIncrementTest.class
                        .getResourceAsStream("/" + OpenSearchDocumentSource.class.getName()
                            + ".icerocket.attributes.xml"))).getDefaultAttributeValueSet()
                .getAttributeValues());
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e);
        }
    }
}
