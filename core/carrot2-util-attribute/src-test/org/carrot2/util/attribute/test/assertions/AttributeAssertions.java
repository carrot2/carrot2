
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

package org.carrot2.util.attribute.test.assertions;

import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.metadata.AttributeMetadata;
import org.carrot2.util.attribute.metadata.CommonMetadata;

/**
 * Assertions for the attribute-related classes.
 */
public class AttributeAssertions
{
    public static CommonMetadataAssertion assertThat(CommonMetadata actual)
    {
        return new CommonMetadataAssertion(actual);
    }

    public static AttributeMetadataAssertion assertThat(AttributeMetadata actual)
    {
        return new AttributeMetadataAssertion(actual);
    }
    
    public static AttributeDescriptorAssertion assertThat(AttributeDescriptor actual)
    {
        return new AttributeDescriptorAssertion(actual);
    }

    public static BindableDescriptorAssertion assertThat(BindableDescriptor actual)
    {
        return new BindableDescriptorAssertion(actual);
    }
}
