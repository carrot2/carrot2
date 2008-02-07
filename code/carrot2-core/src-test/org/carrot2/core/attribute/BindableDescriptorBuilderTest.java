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
    public void testSimpleComponent() throws Exception
    {
        final Object instance = new SingleClass();
        final Class<?> clazz = SingleClass.class;

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptors).isEmpty();
        assertThat(bindableDescriptor.attributeDescriptors.values())
            .contains(
                new AttributeDescriptor(clazz.getDeclaredField("initInputInt"), 10, null,
                    new AttributeMetadata("Init input int attribute", "Init Input Int",
                        null)),
                new AttributeDescriptor(clazz.getDeclaredField("processingInputString"),
                    "test", null, new AttributeMetadata(
                        "Processing input string attribute", "Processing Input String",
                        "Some description.")));
        assertThat(bindableDescriptor.attributeDescriptors.keySet()).excludes(
            clazz.getName() + ".notAnAttribute");
    }

    @Test
    public void testSubClass() throws Exception
    {
        final Object instance = new SubClass();
        final Class<?> subClass = SubClass.class;
        final Class<?> superClass = SuperClass.class;

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        assertThat(bindableDescriptor.bindableDescriptors).isEmpty();
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(superClass.getDeclaredField("initInputInt"), 5, null,
                new AttributeMetadata("Super class init input int", null, null)),
            new AttributeDescriptor(subClass.getDeclaredField("processingInputString"),
                "input", null, new AttributeMetadata("Subclass processing input", null,
                    null)));
    }

    @Test
    public void testBindableReferenceWithDefaultValue() throws Exception
    {
        final Object instance = new BindableReferenceContainer();
        final Class<?> bindableReferenceClass = BindableReferenceContainer.class;
        final Class<?> referenceClass = TestBindableImpl1.class;

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        // Direct attributes
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(bindableReferenceClass
                .getDeclaredField("bindableAttribute"), null, null,
                new AttributeMetadata("Test Bindable", null, null)));

        // Referenced attributes
        Map<String, AttributeDescriptor> referenceAttributeDescriptors = Maps
            .newHashMap();
        referenceAttributeDescriptors.put(referenceClass.getName()
            + ".processingInputInt", new AttributeDescriptor(referenceClass
            .getDeclaredField("processingInputInt"), 10, null, new AttributeMetadata(
            "Processing input int", null, null)));

        assertThat(bindableDescriptor.bindableDescriptors.values())
            .contains(
                new BindableDescriptor(new BindableMetadata(),
                    new HashMap<String, BindableDescriptor>(),
                    referenceAttributeDescriptors));
    }

    @Test
    public void testBindableReferenceWithBoundValue() throws Exception
    {
        final Object instance = new BindableReferenceContainer();
        final Class<?> bindableReferenceClass = BindableReferenceContainer.class;
        final Class<?> referenceClass1 = TestBindableImpl1.class;
        final Class<?> referenceClass2 = TestBindableImpl2.class;

        // Bind init input attributes
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(BindableReferenceContainer.class.getName() + ".bindableAttribute",
            TestBindableImpl2.class);
        AttributeBinder.bind(instance, attributes, Init.class, Input.class);

        BindableDescriptor bindableDescriptor = BindableDescriptorBuilder
            .buildDescriptor(instance);

        // Direct attributes
        assertThat(bindableDescriptor.attributeDescriptors.values()).contains(
            new AttributeDescriptor(bindableReferenceClass
                .getDeclaredField("bindableAttribute"), null, null,
                new AttributeMetadata("Test Bindable", null, null)));

        // Referenced attributes -- regular field
        Map<String, AttributeDescriptor> fieldReferenceAttributeDescriptors = Maps
            .newHashMap();
        fieldReferenceAttributeDescriptors.put(referenceClass1.getName()
            + ".processingInputInt", new AttributeDescriptor(referenceClass1
            .getDeclaredField("processingInputInt"), 10, null, new AttributeMetadata(
            "Processing input int", null, null)));

        assertThat(bindableDescriptor.bindableDescriptors.values()).contains(
            new BindableDescriptor(new BindableMetadata(),
                new HashMap<String, BindableDescriptor>(),
                fieldReferenceAttributeDescriptors));

        // Referenced attributes -- attribute field
        Map<String, AttributeDescriptor> attributeReferenceAttributeDescriptors = Maps
            .newHashMap();
        attributeReferenceAttributeDescriptors.put(referenceClass2.getName()
            + ".initInputInt", new AttributeDescriptor(referenceClass2
            .getDeclaredField("initInputInt"), 12, null, new AttributeMetadata(
            "Init input int", null, null)));

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
