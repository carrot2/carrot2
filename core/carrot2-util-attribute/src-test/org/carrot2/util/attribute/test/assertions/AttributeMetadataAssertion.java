package org.carrot2.util.attribute.test.assertions;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.util.attribute.AttributeMetadata;

/**
 * Assertions on {@link AttributeMetadata}.
 */
public class AttributeMetadataAssertion
{
    private final AttributeMetadata actual;

    public AttributeMetadataAssertion(AttributeMetadata actual)
    {
        this.actual = actual;
    }

    public AttributeMetadataAssertion isEquivalentTo(AttributeMetadata expected)
    {
        new CommonMetadataAssertion(actual).isEquivalentTo(expected);
        assertThat(actual.getLevel()).as("level").isEqualTo(expected.getLevel());
        assertThat(actual.getGroup()).as("group").isEqualTo(expected.getGroup());
        return this;
    }
}
