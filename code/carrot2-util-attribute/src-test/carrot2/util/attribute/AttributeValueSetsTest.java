/**
 *
 */
package carrot2.util.attribute;

import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.junit.Test;

/**
 *
 */
public class AttributeValueSetsTest
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
    }

    @Test
    public void testSerializationDeserializationEmpty() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationNoValues() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);

        sets.addAttributeValueSet("set1", set1, "Set 1", "Description 1");

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationPrimitiveValues() throws Exception
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

    static class CustomClass
    {
        String value1;
        String value2;

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

        public static CustomClass valueOf(String string)
        {
            final String [] split = string.split(":");
            return new CustomClass(split[0], split[1]);
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
    public void testSerializationDeserializationCustomClass() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", new CustomClass("t1", "t2"));

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationNullValue() throws Exception
    {
        final AttributeValueSets sets = new AttributeValueSets();

        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1",
            null);
        set1.setAttributeValue("att1", null);

        sets.addAttributeValueSet("set1", set1);

        checkSerializationDeserialization(sets);
    }

    @Test
    public void testSerializationDeserializationBaseReferences() throws Exception
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

    private void checkSerializationDeserialization(AttributeValueSets sets)
        throws Exception
    {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sets.serialize(byteArrayOutputStream);

        final AttributeValueSets deserialized = AttributeValueSets
            .deserialize(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

        for (final Map.Entry<String, AttributeValueSet> entry : sets.attributeValueSets
            .entrySet())
        {
            final AttributeValueSet attributeValueSet = entry.getValue();
            assertEqualsAttributeValueSet(attributeValueSet, deserialized
                .getAttributeValueSet(entry.getKey()));

            // The base attribute value set must be in the sets (this way we'll
            // know the references have been properly deserialized)
            if (attributeValueSet.baseAttributeValueSet != null)
            {
                deserialized.attributeValueSets
                    .containsValue(attributeValueSet.baseAttributeValueSet);
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
