package org.carrot2.examples.core;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.util.Map;
import java.util.Set;

import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.junit.Test;


/**
 * An example showing how developers can use the low-level core utilities for working with
 * attribute value sets. A general example is provided {@link #example()} as well as one
 * that might resemble the actual use case within the RCP browser
 * {@link #potentialUseInRcpBrowser()}.
 */
public class WorkingWithAttributeValueSets
{
    @Test
    public void example() throws Exception
    {
        /**
         * An AttributeValueSet groups a number of attribute values. It can have a label,
         * (optionally) description and (optionally) a base attribute set from which it
         * can "inherit" attribute values.
         */
        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1", null);
        final AttributeValueSet set2 = new AttributeValueSet("Set 2", "Description 2", set1);

        /**
         * You can easily set and retrieve attribute values.
         */
        set1.setAttributeValue("string.attribute", "value");
        set1.setAttributeValue("integer.attribute", 10);
        assertEquals("value", set1.getAttributeValue("string.attribute"));
        assertEquals(10, set1.getAttributeValue("integer.attribute"));

        /**
         * Notice that set2 inherits values from set1.
         */
        assertEquals("value", set2.getAttributeValue("string.attribute"));

        /**
         * We can also override the inherited value.
         */
        set2.setAttributeValue("string.attribute", "overriden");
        assertEquals("overriden", set2.getAttributeValue("string.attribute"));
        assertEquals(10, set2.getAttributeValue("integer.attribute"));

        /**
         * We can group attribute value sets.
         */
        final AttributeValueSets sets = new AttributeValueSets();
        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);

        /**
         * The most important functionality of AttributeValueSets is serialization/
         * deserialization from XML.
         */
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sets.serialize(byteArrayOutputStream);
        CloseableUtils.close(byteArrayOutputStream); // need to close!

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            byteArrayOutputStream.toByteArray());
        final AttributeValueSets deserialized = AttributeValueSets
            .deserialize(byteArrayInputStream);
        CloseableUtils.close(byteArrayInputStream); // need to close!

        assertEquals("value", deserialized.getAttributeValueSet("set1")
            .getAttributeValue("string.attribute"));
        assertEquals(10, deserialized.getAttributeValueSet("set1").getAttributeValue(
            "integer.attribute"));
        assertEquals("overriden", deserialized.getAttributeValueSet("set2")
            .getAttributeValue("string.attribute"));
        assertEquals(10, deserialized.getAttributeValueSet("set2").getAttributeValue(
            "integer.attribute"));
    }

    @Test
    @SuppressWarnings("unused")
    public void potentialUseInRcpBrowser() throws Exception
    {
        /**
         * The general assumption is that somewhere there will be one or more XML
         * resources (e.g. file) with example attribute sets. When the attribute editing
         * panel is initialized, the browser will probably need to load the appropriate
         * attribute value sets.
         */
        final AttributeValueSets sets = AttributeValueSets
            .deserialize(getAttributeValueSetsStream());

        /**
         * To build the model for e.g. a combo box used to select the attribute value set,
         * you'll need to get list of setIds and then iterate to get e.g. labels.
         */
        final Set<String> attributeValueSetIds = sets.getAttributeValueSetIds();

        /**
         * When a user selects some set, you'll probably need to get a map of all values
         * the set has, including the actual and inherited ones.
         */
        final AttributeValueSet selectedAttributeValueSet = sets
            .getAttributeValueSet("set1");
        final Map<String, Object> attributeValues = selectedAttributeValueSet
            .getAttributeValues();

        /**
         * When a user modifies value of some attribute, you may need to create a
         * temporary attribute value set based on the currently selected "named" attribute
         * value set and set the value in there. The combo box could get an entry along
         * the lines of "Custom" and make it selected.
         */
        final AttributeValueSet customAttributeValueSet = new AttributeValueSet("Custom", null,
            selectedAttributeValueSet);
        customAttributeValueSet.setAttributeValue("modified.attribute.key", "value");

        /**
         * Notice that until the user chooses to save the "Custom" attribute value set
         * under some name, you should not add it to the AttributeValueSets, otherwise it
         * would get serialized and cause confusion.
         * <p>
         * When the user chooses to save the custom set under some name, we can add it to
         * our AttributeValueSets and (possibly) serialize everything to XML to avoid data
         * loss should the application crash.
         */
        sets.addAttributeValueSet("set3", customAttributeValueSet, "Set 3",
            "Description if any");
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sets.serialize(byteArrayOutputStream);
        CloseableUtils.close(byteArrayOutputStream); // need to close!
    }

    private InputStream getAttributeValueSetsStream() throws Exception
    {
        final AttributeValueSet set1 = new AttributeValueSet("Set 1", "Description 1", null);
        final AttributeValueSet set2 = new AttributeValueSet("Set 2", "Description 2", set1);

        set1.setAttributeValue("string.attribute", "value");
        set1.setAttributeValue("integer.attribute", 10);
        set2.setAttributeValue("string.attribute", "overriden");

        final AttributeValueSets sets = new AttributeValueSets();
        sets.addAttributeValueSet("set1", set1);
        sets.addAttributeValueSet("set2", set2);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sets.serialize(byteArrayOutputStream);
        CloseableUtils.close(byteArrayOutputStream); // need to close!

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
