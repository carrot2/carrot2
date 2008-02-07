/**
 * 
 */
package org.carrot2.core.attribute;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.carrot2.core.attribute.test.filtering.*;
import org.junit.Test;

/**
 *
 */
public class BindableDescriptorTest
{
    private BindableDescriptor descriptor = BindableDescriptorBuilder
        .buildDescriptor(new FilteringSubClass());

    @Test
    public void testOnlyWithNoCriteria()
    {
        BindableDescriptor filteredDescriptor = descriptor.only();
        assertEquals(descriptor, filteredDescriptor);
    }

    @Test
    public void testOnlyInitProcessing()
    {
        BindableDescriptor filteredDescriptor = descriptor.only(Init.class,
            Processing.class);
        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSubClass("initProcessingInput"),
            keyFromSubClass("initProcessingOutput"),
            keyFromSubClass("initProcessingInputOutput"));
        assertThat(getReferenceDescriptors(filteredDescriptor)).isEmpty();
    }

    @Test
    public void testOnlyInputOutput()
    {
        BindableDescriptor filteredDescriptor = descriptor
            .only(Input.class, Output.class);
        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSuperClass("initInputOutput"),
            keyFromSuperClass("processingInputOutput"),
            keyFromSubClass("initProcessingInputOutput"));
        assertThat(getReferenceDescriptors(filteredDescriptor)).isEmpty();
    }

    @Test
    public void testOnlyInputOutputInitProcessing()
    {
        BindableDescriptor filteredDescriptor = descriptor.only(Init.class,
            Processing.class, Input.class, Output.class);
        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSubClass("initProcessingInputOutput"));
        assertThat(getReferenceDescriptors(filteredDescriptor)).isEmpty();
    }

    @Test
    public void testOnlyInput()
    {
        BindableDescriptor filteredDescriptor = descriptor.only(Input.class);

        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSuperClass("initInput"), keyFromSuperClass("initInputOutput"),
            keyFromSuperClass("processingInput"),
            keyFromSuperClass("processingInputOutput"),
            keyFromSubClass("initProcessingInput"),
            keyFromSubClass("initProcessingInputOutput"));

        assertThat(getReferenceDescriptors(filteredDescriptor).keySet()).containsOnly(
            keyFromReferenceClass("initInput"), keyFromReferenceClass("processingInput"));
    }

    @Test
    public void testOnlyInputProcessing()
    {
        BindableDescriptor filteredDescriptor = descriptor.only(Input.class,
            Processing.class);

        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSuperClass("processingInput"),
            keyFromSuperClass("processingInputOutput"),
            keyFromSubClass("initProcessingInput"),
            keyFromSubClass("initProcessingInputOutput"));

        assertThat(getReferenceDescriptors(filteredDescriptor).keySet()).containsOnly(
            keyFromReferenceClass("processingInput"));
    }

    @Test
    public void testFlatten()
    {
        BindableDescriptor filteredDescriptor = descriptor.only(Input.class,
            Processing.class).flatten();

        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSuperClass("processingInput"),
            keyFromSuperClass("processingInputOutput"),
            keyFromSubClass("initProcessingInput"),
            keyFromSubClass("initProcessingInputOutput"),
            keyFromReferenceClass("processingInput"));

        assertThat(filteredDescriptor.bindableDescriptors).isEmpty();
    }
    
    private Map<String, AttributeDescriptor> getReferenceDescriptors(
        BindableDescriptor filteredDescriptor)
    {
        return filteredDescriptor.bindableDescriptors.get("reference").attributeDescriptors;
    }

    private String keyFromSuperClass(String fieldName)
    {
        return FilteringSuperClass.class.getName() + "." + fieldName;
    }

    private String keyFromSubClass(String fieldName)
    {
        return FilteringSubClass.class.getName() + "." + fieldName;
    }

    private String keyFromReferenceClass(String fieldName)
    {
        return FilteringReferenceClass.class.getName() + "." + fieldName;
    }
}
