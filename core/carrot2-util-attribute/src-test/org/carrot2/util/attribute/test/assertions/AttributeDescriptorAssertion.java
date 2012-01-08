
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.test.assertions;

import static org.carrot2.util.attribute.test.assertions.AttributeAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.util.attribute.AttributeDescriptor;

/**
 * Assertions on {@link AttributeDescriptor}s.
 */
public class AttributeDescriptorAssertion
{
    private final AttributeDescriptor actual;

    public AttributeDescriptorAssertion(AttributeDescriptor actual)
    {
        this.actual = actual;
    }

    public AttributeDescriptorAssertion isEquivalentTo(AttributeDescriptor expected)
    {
        assertThat(actual.constraints).as("constraints").isEqualTo(expected.constraints);
        assertThat(actual.defaultValue).as("defaultValue").isEqualTo(
            expected.defaultValue);
        assertThat(actual.inputAttribute).as("inputAttribute").isEqualTo(
            expected.inputAttribute);
        assertThat(actual.key).as("key").isEqualTo(expected.key);
        assertThat(actual.outputAttribute).as("outputAttribute").isEqualTo(
            expected.outputAttribute);
        assertThat(actual.requiredAttribute).as("requiredAttribute").isEqualTo(
            expected.requiredAttribute);
        assertThat(actual.type).as("type").isEqualTo(expected.type);
        assertThat(actual.metadata).isEquivalentTo(expected.metadata);

        return this;
    }
}
