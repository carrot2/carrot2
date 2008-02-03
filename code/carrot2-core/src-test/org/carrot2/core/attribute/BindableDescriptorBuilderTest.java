/**
 * 
 */
package org.carrot2.core.attribute;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.attribute.test.*;
import org.junit.Test;

/**
 *
 */
public class BindableDescriptorBuilderTest
{
    @Test
    public void testSimpleComponent()
    {
        Object instance = new SingleClass();
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
        Object instance = new SubClass();
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
}
