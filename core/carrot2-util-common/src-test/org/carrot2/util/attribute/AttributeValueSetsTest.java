
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import org.simpleframework.xml.Root;

public class AttributeValueSetsTest extends CarrotTestCase
{
    @Test(expected = IllegalArgumentException.class)
    public void testAddingWithExistingId()
    {
        final AttributeValueSets sets = new AttributeValueSets();

        sets.addAttributeValueSet("set1", new AttributeValueSet(null, null, null));
        sets.addAttributeValueSet("set1", new AttributeValueSet(null, null, null));
    }

    @Test
    public void testAttributeValueSetsBasedOn()
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet(null, null, null);
        final AttributeValueSet set2 = new AttributeValueSet(null, null, set1);
        final AttributeValueSet set3 = new AttributeValueSet(null, null, set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        assertThat(sets.getAttributeValueSetsBasedOn(set1)).containsOnly(set2, set3);
        assertThat(sets.getAttributeValueSetsBasedOn(set2)).containsOnly(set3);
        assertThat(sets.getAttributeValueSetsBasedOn(set3)).isEmpty();
    }

    @Test
    public void testRemovingAttributeValueSet()
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet(null, null, null);
        final AttributeValueSet set2 = new AttributeValueSet(null, null, set1);
        final AttributeValueSet set3 = new AttributeValueSet(null, null, set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        sets.removeAttributeValueSet("set2");

        // Attempt to remove a nonexisting set should not fail
        sets.removeAttributeValueSet("nonexisting");

        assertEquals(set1.baseAttributeValueSet, set3.baseAttributeValueSet);
        assertNull(sets.getAttributeValueSet("set2"));
    }

    @Test
    public void testGetDefaultWithNoDefaultProvided()
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet(null, null, null);
        final AttributeValueSet set2 = new AttributeValueSet(null, null, set1);
        final AttributeValueSet set3 = new AttributeValueSet(null, null, set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        sets.setDefaultAttributeValueSetId(null);

        assertSame(sets.getDefaultAttributeValueSet(), set1);
    }

    @Test
    public void testGetDefaultWithDefaultProvided()
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet(null, null, null);
        final AttributeValueSet set2 = new AttributeValueSet(null, null, set1);
        final AttributeValueSet set3 = new AttributeValueSet(null, null, set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        sets.setDefaultAttributeValueSetId("set3");

        assertSame(sets.getAttributeValueSet("set5", true), set3);
    }

    @Test
    public void testGetDefaultWithEmptyAttributeValueSets()
    {
        final AttributeValueSets sets = new AttributeValueSets();
        assertNull(sets.getDefaultAttributeValueSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalSetDefaultAttributeValueSetId()
    {
        final AttributeValueSets sets = new AttributeValueSets();
        sets.setDefaultAttributeValueSetId("set1");
    }

    @Test
    public void testSerializationDeserializationOfEmpty() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationWithNoValues() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);

        sets.addAttributeValueSet("set1", set1, "Set 1", "Description 1");

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationOfPrimitiveValues() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", "value");
        set1.setAttributeValue("att2", 0.5);
        set1.setAttributeValue("att3", 10);
        set1.setAttributeValue("att4", ArrayList.class);

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    public static enum CustomEnum
    {
        ABC, DEF;

        public String toString()
        {
            return this.name().toLowerCase();
        }
    }

    @Test
    public void testSerializationDeserializationEnumValues() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", CustomEnum.ABC);

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    @Root(name = "simple")
    static class CustomClass
    {
        @org.simpleframework.xml.Attribute
        String value1;

        @org.simpleframework.xml.Attribute
        String value2;

        CustomClass()
        {
        }

        CustomClass(String value1, String value2)
        {
            this.value1 = value1;
            this.value2 = value2;
        }

        @Override
        public String toString()
        {
            return value1 + ":" + value2;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == null || !(obj instanceof CustomClass))
            {
                return false;
            }

            final CustomClass other = (CustomClass) obj;

            return ObjectUtils.equals(value1, other.value1)
                && ObjectUtils.equals(value2, other.value2);
        }

        @Override
        public int hashCode()
        {
            return ObjectUtils.hashCode(value1) ^ ObjectUtils.hashCode(value2);
        }
    }
    
    @Test
    public void testSerializationDeserializationOfCustomClass() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", new CustomClass("t1", "t2"));

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationOfNullValue() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", null);

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationOfBaseReferences() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        final AttributeValueSet set2 = new AttributeValueSet("Set 2", "Description 2",
            set1);
        final AttributeValueSet set3 = new AttributeValueSet("Set 3", "Description 3",
            set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationOfDefaultAttributeValueSetId()
        throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", null, null);
        final AttributeValueSet set2 = new AttributeValueSet("Set 2", null, set1);
        final AttributeValueSet set3 = new AttributeValueSet("Set 3", null, set2);

        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);
        sets.addAttributeValueSet("set3", set3);

        sets.setDefaultAttributeValueSetId("set3");

        checkSerializationDeserialization(sets);
    }

    @Test(expected = RuntimeException.class)
    public void testSerializationDeserializationOfIllegalDefaultAttributeValueSetId()
        throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();
        final AttributeValueSet set1 = new AttributeValueSet("Set 1", null, null);
        sets.addAttributeValueSet("set1", set1);

        Field field = AttributeValueSets.class
            .getDeclaredField("defaultAttributeValueSetId");
        field.set(sets, "set3");
        checkSerializationDeserialization(sets);
    }

    private void checkSerializationDeserialization(AttributeValueSets sets)
        throws Exception
    {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        sets.serialize(baos);

        final AttributeValueSets deserialized = AttributeValueSets
            .deserialize(new ByteArrayInputStream(baos.toByteArray()));

        assertEquals(sets.defaultAttributeValueSetId,
            deserialized.defaultAttributeValueSetId);

        // The default attribute value set must exist
        if (deserialized.defaultAttributeValueSetId != null)
        {
            assertTrue(deserialized.attributeValueSets
                .containsKey(deserialized.defaultAttributeValueSetId));
        }

        for (final Map.Entry<String, AttributeValueSet> entry : sets.attributeValueSets
            .entrySet())
        {
            final AttributeValueSet attributeValueSet = entry.getValue();
            assertEqualsAttributeValueSet(attributeValueSet, deserialized
                .getAttributeValueSet(entry.getKey()));
        }

        for (final Map.Entry<String, AttributeValueSet> entry : deserialized.attributeValueSets
            .entrySet())
        {
            final AttributeValueSet attributeValueSet = entry.getValue();

            // The base attribute value set must be in the sets (this way we'll
            // know the references have been properly deserialized)
            if (attributeValueSet.baseAttributeValueSet != null)
            {
                assertTrue(deserialized.attributeValueSets
                    .containsValue(attributeValueSet.baseAttributeValueSet));
            }
        }
    }

    private void assertEqualsAttributeValueSet(AttributeValueSet expected,
        AttributeValueSet actual)
    {
        assertEquals(expected.label, actual.label);
        assertEquals(expected.description, actual.description);
        assertEquals(expected.overridenAttributeValues, actual.overridenAttributeValues);
    }
}
