
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

package org.carrot2.util.attribute;

import static org.fest.assertions.MapAssert.entry;

import java.util.Map;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.Maps;

public class AttributeValueSetTest extends CarrotTestCase
{
    @Test
    public void testNullBaseAttributeValueSet()
    {
        final AttributeValueSet set = new AttributeValueSet(null, null, null);
        set.setAttributeValue("testKey", "testValue");

        assertEquals("testValue", set.getAttributeValue("testKey"));
    }

    @Test
    public void testOneLevelBaseAttributeValueSet()
    {
        final AttributeValueSet base = new AttributeValueSet(null, null, null);
        base.setAttributeValue("baseKey", "baseValue");
        base.setAttributeValue("overridenKey", "overrideMe");

        final AttributeValueSet set = new AttributeValueSet(null, null, base);
        set.setAttributeValue("testKey", "testValue");
        set.setAttributeValue("overridenKey", "overridenValue");

        assertEquals("testValue", set.getAttributeValue("testKey"));
        assertEquals("baseValue", set.getAttributeValue("baseKey"));
        assertEquals("overridenValue", set.getAttributeValue("overridenKey"));
        assertEquals(null, set.getAttributeValue("nonexistingKey"));

        assertThat(set.getAttributeValues()).includes(entry("testKey", "testValue"),
            entry("baseKey", "baseValue"), entry("overridenKey", "overridenValue"))
            .hasSize(3);
    }

    @Test
    public void testMultiLevelBaseAttributeValueSet()
    {
        final AttributeValueSet base1 = new AttributeValueSet(null, null, null);
        base1.setAttributeValue("base1Key", "base1Value");
        base1.setAttributeValue("overriden1Key", "override1Me");

        final AttributeValueSet base2 = new AttributeValueSet(null, null, base1);
        final Map<String, Object> base2Values = Maps.newHashMap();
        base2Values.put("base2Key", "base2Value");
        base2Values.put("overriden2Key", "overrideMe");
        base2.setAttributeValues(base2Values);

        final AttributeValueSet set = new AttributeValueSet(null, null, base2);
        set.setAttributeValue("testKey", "testValue");
        set.setAttributeValue("overriden1Key", "overriden1Value");

        assertEquals("testValue", set.getAttributeValue("testKey"));
        assertEquals("base1Value", set.getAttributeValue("base1Key"));
        assertEquals("overriden1Value", set.getAttributeValue("overriden1Key"));
        assertEquals("overrideMe", set.getAttributeValue("overriden2Key"));

        assertThat(set.getAttributeValues()).includes(entry("testKey", "testValue"),
            entry("base1Key", "base1Value"), entry("base2Key", "base2Value"),
            entry("overriden1Key", "overriden1Value"),
            entry("overriden2Key", "overrideMe")).hasSize(5);
    }
}
