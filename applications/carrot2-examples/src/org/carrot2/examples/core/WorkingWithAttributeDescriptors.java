/**
 *
 */
package org.carrot2.examples.core;

import java.util.*;
import java.util.Map.Entry;

import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.examples.ExampleUtils;
import org.carrot2.source.yahoo.YahooDocumentSource;
import org.carrot2.source.yahoo.YahooNewsSearchService;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * An example showing how to use the low-level core utilities for processing Carrot2
 * component attribute descriptors.
 * <p>
 * Before you run this example, you need to run {@link BindableMetadataXmlSerializer} (to
 * generate metadata about components). There is an Eclipse launch configuration that does
 * just that in <code>carrot2-util-attributes/etc/eclipse</code>. Alternatively, run
 * the master ANT build script (<code>build.xml</code>):
 * <code>ant attrs attrs.eclipse</code>.
 */
public class WorkingWithAttributeDescriptors
{
    @SuppressWarnings("unchecked")
    public static void main(String [] args) throws InstantiationException
    {
        // Here is the component instance that we will be working with. Notice
        // that the BindableDescriptorBuilder requires an initialized instance on input.
        final YahooDocumentSource yahooDocumentSource = new YahooDocumentSource();

        // Descriptors for a component with default initialization attribute values
        BindableDescriptor descriptor = BindableDescriptorBuilder
            .buildDescriptor(yahooDocumentSource);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // Notice that for some values of initialization attribute values,
        // you may get different attribute descriptors
        final Map<String, Object> initAttributes = new HashMap<String, Object>();
        initAttributes.put(AttributeUtils.getKey(YahooDocumentSource.class, "service"),
            YahooNewsSearchService.class);
        AttributeBinder
            .bind(yahooDocumentSource, initAttributes, Input.class, Init.class);

        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // You can also get descriptors for a subset of component's attributes
        // (in the case below, only input attributes bound at processing time)
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource).only(
            Input.class, Processing.class);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // You group descriptors by different keys
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource)
            .group(BindableDescriptor.GroupingMethod.LEVEL);
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // You can also flatten the descriptor tree to a linear list
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource).only(
            Input.class, Processing.class).flatten();
        displayDescriptor(descriptor, 0);
        System.out.println("\n");

        // For attributes whose value is not a primitive type (e.g. see
        // YahooDocumentSource.service), you may want to get a list of all allowed
        // implementing classes to be shown e.g. in a combo box.
        descriptor = BindableDescriptorBuilder.buildDescriptor(yahooDocumentSource);
        final AttributeDescriptor yahooServiceDescriptor = descriptor.attributeDescriptors
            .get(AttributeUtils.getKey(YahooDocumentSource.class, "service"));
        System.out.println(yahooServiceDescriptor.metadata.getLabel()
            + ": allowed implementations: "
            + Arrays.toString(((ImplementingClasses) yahooServiceDescriptor.constraints
                .get(0)).classes()));
    }

    private static void displayDescriptor(BindableDescriptor descriptor, int indent)
    {
        for (final AttributeDescriptor attributeDescriptor : descriptor.attributeDescriptors
            .values())
        {
            displayDescriptor(attributeDescriptor, indent);
        }

        for (final Entry<Object, Map<String, AttributeDescriptor>> entry : descriptor.attributeGroups
            .entrySet())
        {
            System.out.println(entry.getKey().toString());
            for (AttributeDescriptor attributeDescriptor : entry.getValue().values())
            {
                displayDescriptor(attributeDescriptor, indent + 2);
            }
        }
    }

    private static void displayDescriptor(final AttributeDescriptor attributeDescriptor,
        int indent)
    {
        System.out.print(ExampleUtils.getIndent(indent));
        System.out.println(attributeDescriptor.metadata.getLabel() + ": "
            + attributeDescriptor.metadata.getTitle());
    }
}
