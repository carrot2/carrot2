package org.carrot2.util.attribute.test.assertions;

import static org.carrot2.util.attribute.test.assertions.AttributeAssertions.assertThat;

import org.carrot2.util.attribute.*;
import org.fest.assertions.Assertions;

/**
 * Assertions on {@link BindableDescriptor}s.
 */
public class BindableDescriptorAssertion
{
    private final BindableDescriptor actual;

    public BindableDescriptorAssertion(BindableDescriptor actual)
    {
        this.actual = actual;
    }

    public BindableDescriptorAssertion contains(String key, AttributeDescriptor expected)
    {
        Assertions.assertThat(actual.attributeDescriptors.get(key)).isNotNull();
        assertThat(actual.attributeDescriptors.get(key)).isEquivalentTo(expected);
        return this;
    }

    public BindableDescriptorAssertion hasMetadata(BindableMetadata metadata)
    {
        // Flat comparison, not comparint internal map of attribute metadata
        assertThat(metadata).isEquivalentTo(metadata);
        return this;
    }

    public BindableDescriptorAssertion isNotNull()
    {
        Assertions.assertThat(actual).isNotNull();
        return this;
    }
}
