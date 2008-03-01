package org.carrot2.util.attribute;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.carrot2.util.attribute.test.filtering.*;
import org.junit.Test;


/**
 *
 */
@SuppressWarnings("unchecked")
public class BindableDescriptorTest
{
    private final BindableDescriptor descriptor = BindableDescriptorBuilder
        .buildDescriptor(new FilteringSubClass());

    @Test
    public void testOnlyWithNoCriteria()
    {
        final BindableDescriptor filteredDescriptor = descriptor.only();
        assertEquals(descriptor, filteredDescriptor);
    }

    @Test
    public void testOnlyInitProcessing()
    {
        final BindableDescriptor filteredDescriptor = descriptor.only(TestInit.class,
            TestProcessing.class);
        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSubClass("initProcessingInput"),
            keyFromSubClass("initProcessingOutput"),
            keyFromSubClass("initProcessingInputOutput"));
        assertThat(getReferenceDescriptors(filteredDescriptor)).isEmpty();
    }

    @Test
    public void testOnlyInputOutput()
    {
        final BindableDescriptor filteredDescriptor = descriptor
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
        final BindableDescriptor filteredDescriptor = descriptor.only(TestInit.class,
            TestProcessing.class, Input.class, Output.class);
        assertThat(filteredDescriptor.attributeDescriptors.keySet()).containsOnly(
            keyFromSubClass("initProcessingInputOutput"));
        assertThat(getReferenceDescriptors(filteredDescriptor)).isEmpty();
    }

    @Test
    public void testOnlyInput()
    {
        final BindableDescriptor filteredDescriptor = descriptor.only(Input.class);

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
        final BindableDescriptor filteredDescriptor = descriptor.only(Input.class,
            TestProcessing.class);

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
        final BindableDescriptor filteredDescriptor = descriptor.only(Input.class,
            TestProcessing.class).flatten();

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
