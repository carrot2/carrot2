/**
 * 
 */
package org.carrot2.core.attribute;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.attribute.test.*;
import org.junit.Test;

import com.google.common.collect.Maps;

/**
 *
 */
public class BindableDescriptorBuilderTest
{
    @Test
    public void testSimpleComponent()
    {
        final Object instance = new SingleClass();
        final String className = SingleClass.class.getName();

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptors).isEmpty();
        assertThat(bindableDescriptor.attributeDescriptors.values())
            .contains(
                new AttributeDescriptor(className + ".initInputInt", Integer.class, 10,
                    null, new AttributeMetadata("Init input int attribute",
                        "Init Input Int", null)),
                new AttributeDescriptor(className + ".processingInputString",
                    String.class, "test", null, new AttributeMetadata(
                        "Processing input string attribute", "Processing Input String",
                        "Some description.")));
        assertThat(bindableDescriptor.attributeDescriptors.keySet()).excludes(
            className + ".notAnAttribute");
    }

    @Test
    public void testSubClass()
    {
        final Object instance = new SubClass();
        final String subClassName = SubClass.class.getName();
        final String superClassName = SuperClass.class.getName();

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptors).isEmpty();
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(superClassName + ".initInputInt", Integer.class, 5,
                null, new AttributeMetadata("Super class init input int", null, null)),
            new AttributeDescriptor(subClassName + ".processingInputString",
                String.class, "input", null, new AttributeMetadata(
                    "Subclass processing input", null, null)));
    }

    @Test
    public void testBindableReferenceWithDefaultValue()
    {
        final Object instance = new BindableReferenceContainer();
        final String bindableReferenceClassName = BindableReferenceContainer.class
            .getName();
        final String referenceClassName = TestBindableImpl1.class.getName();

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        // Direct attributes
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(bindableReferenceClassName + ".bindableAttribute",
                TestBindable.class, null, null, new AttributeMetadata("Test Bindable",
                    null, null)));

        // Referenced attributes
        Map<String, AttributeDescriptor> referenceAttributeDescriptors = Maps
            .newHashMap();
        referenceAttributeDescriptors.put(referenceClassName + ".processingInputInt",
            new AttributeDescriptor(referenceClassName + ".processingInputInt",
                Integer.class, 10, null, new AttributeMetadata("Processing input int",
                    null, null)));

        assertThat(bindableDescriptor.bindableDescriptors.values())
            .contains(
                new BindableDescriptor(new BindableMetadata(),
                    new HashMap<String, BindableDescriptor>(),
                    referenceAttributeDescriptors));
    }

    @Test
    public void testBindableReferenceWithBoundValue() throws InstantiationException
    {
        final Object instance = new BindableReferenceContainer();
        final String bindableReferenceClassName = BindableReferenceContainer.class
            .getName();
        final String referenceClassName1 = TestBindableImpl1.class.getName();
        final String referenceClassName2 = TestBindableImpl2.class.getName();

        // Bind init input attributes
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(BindableReferenceContainer.class.getName() + ".bindableAttribute",
            TestBindableImpl2.class);
        AttributeBinder.bind(instance, attributes, Init.class, Input.class);

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        // Direct attributes
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(bindableReferenceClassName + ".bindableAttribute",
                TestBindable.class, null, null, new AttributeMetadata("Test Bindable",
                    null, null)));

        // Referenced attributes -- regular field
        Map<String, AttributeDescriptor> fieldReferenceAttributeDescriptors = Maps
            .newHashMap();
        fieldReferenceAttributeDescriptors.put(referenceClassName1
            + ".processingInputInt", new AttributeDescriptor(referenceClassName1
            + ".processingInputInt", Integer.class, 10, null, new AttributeMetadata(
            "Processing input int", null, null)));

        assertThat(bindableDescriptor.bindableDescriptors.values()).contains(
            new BindableDescriptor(new BindableMetadata(),
                new HashMap<String, BindableDescriptor>(),
                fieldReferenceAttributeDescriptors));

        // Referenced attributes -- attribute field
        Map<String, AttributeDescriptor> attributeReferenceAttributeDescriptors = Maps
            .newHashMap();
        attributeReferenceAttributeDescriptors.put(referenceClassName2 + ".initInputInt",
            new AttributeDescriptor(referenceClassName2 + ".initInputInt", Integer.class,
                12, null, new AttributeMetadata("Init input int", null, null)));

        assertThat(bindableDescriptor.bindableDescriptors.values()).contains(
            new BindableDescriptor(new BindableMetadata(),
                new HashMap<String, BindableDescriptor>(),
                attributeReferenceAttributeDescriptors));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCircularReference()
    {
        final CircularReferenceContainer instance = new CircularReferenceContainer();
        instance.circular = instance;

        BindableDescriptorBuilder.buildDescriptor(instance);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotBindable()
    {
        BindableDescriptorBuilder.buildDescriptor(new NotBindable());
    }
}
