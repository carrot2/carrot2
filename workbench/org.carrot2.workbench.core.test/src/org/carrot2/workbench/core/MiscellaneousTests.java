package org.carrot2.workbench.core;

import java.io.*;

import junit.framework.TestCase;

import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.resource.FileResource;

public class MiscellaneousTests extends TestCase
{

    public void testResourceDeserialization() throws Exception
    {
        AttributeValueSets sets = new AttributeValueSets();
        AttributeValueSet set = new AttributeValueSet("set1");
        set.setAttributeValue("file.resource", new FileResource(new File(".")));
        sets.addAttributeValueSet("set1", set);
        checkSerializationDeserialization(sets, "set1");
    }

    private void checkSerializationDeserialization(AttributeValueSets sets, String setId)
        throws Exception
    {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        sets.serialize(byteArrayOutputStream);

        final AttributeValueSets deserialized =
            AttributeValueSets.deserialize(new ByteArrayInputStream(byteArrayOutputStream
                .toByteArray()));

        final AttributeValueSet attributeValueSet = sets.getAttributeValueSet(setId);
        assertEqualsAttributeValueSet(attributeValueSet, deserialized
            .getAttributeValueSet(setId));
    }

    private void assertEqualsAttributeValueSet(AttributeValueSet expected,
        AttributeValueSet actual)
    {
        assertEquals(expected.label, actual.label);
        assertEquals(expected.description, actual.description);
    }

}
