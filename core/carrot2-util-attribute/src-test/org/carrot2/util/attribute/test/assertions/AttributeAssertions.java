package org.carrot2.util.attribute.test.assertions;

import org.carrot2.util.attribute.*;

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
