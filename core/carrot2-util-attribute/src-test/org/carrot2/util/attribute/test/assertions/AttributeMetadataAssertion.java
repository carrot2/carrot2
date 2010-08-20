
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

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.util.attribute.metadata.AttributeMetadata;

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
