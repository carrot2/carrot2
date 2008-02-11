/**
 * 
 */
package org.carrot2.core.attribute;

import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

/**
 *
 */
public class AttributeValueSetTest
{
    @Test
    public void testNullBaseAttributeValueSet()
    {
        AttributeValueSet set = new AttributeValueSet(null, null, null);
        set.setAttributeValue("testKey", "testValue");

        assertEquals("testValue", set.getAttributeValue("testKey"));
    }

    @Test
    public void testOneLevelBaseAttributeValueSet()
    {
        AttributeValueSet base = new AttributeValueSet(null, null, null);
        base.setAttributeValue("baseKey", "baseValue");
        base.setAttributeValue("overridenKey", "overrideMe");

        AttributeValueSet set = new AttributeValueSet(null, null, base);
        set.setAttributeValue("testKey", "testValue");
        set.setAttributeValue("overridenKey", "overridenValue");

        assertEquals("testValue", set.getAttributeValue("testKey"));
        assertEquals("baseValue", set.getAttributeValue("baseKey"));
        assertEquals("overridenValue", set.getAttributeValue("overridenKey"));
        assertEquals(null, set.getAttributeValue("nonexistingKey"));

        assertThat(set.getAttributeValues()).contains(entry("testKey", "testValue"),
            entry("baseKey", "baseValue"), entry("overridenKey", "overridenValue"))
            .hasSize(3);
    }

    @Test
    public void testMultiLevelBaseAttributeValueSet()
    {
        AttributeValueSet base1 = new AttributeValueSet(null, null, null);
        base1.setAttributeValue("base1Key", "base1Value");
        base1.setAttributeValue("overriden1Key", "override1Me");

        AttributeValueSet base2 = new AttributeValueSet(null, null, base1);
        Map<String, Object> base2Values = Maps.newHashMap();
        base2Values.put("base2Key", "base2Value");
        base2Values.put("overriden2Key", "overrideMe");
        base2.setAttributeValues(base2Values);

        AttributeValueSet set = new AttributeValueSet(null, null, base2);
        set.setAttributeValue("testKey", "testValue");
        set.setAttributeValue("overriden1Key", "overriden1Value");

        assertEquals("testValue", set.getAttributeValue("testKey"));
        assertEquals("base1Value", set.getAttributeValue("base1Key"));
        assertEquals("overriden1Value", set.getAttributeValue("overriden1Key"));
        assertEquals("overrideMe", set.getAttributeValue("overriden2Key"));

        assertThat(set.getAttributeValues()).contains(entry("testKey", "testValue"),
            entry("base1Key", "base1Value"), entry("base2Key", "base2Value"),
            entry("overriden1Key", "overriden1Value"),
            entry("overriden2Key", "overrideMe")).hasSize(5);
    }
}
