package org.carrot2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.resource.ContextClassLoaderLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Dumps information on processing components from the provided suite into one XML file.
 */
public class ProcessingComponentDumper
{
    @Root(name = "processing-component-docs")
    static class ProcessingComponentDocs
    {
        @ElementList
        final ArrayList<ProcessingComponentDoc> sources;

        @ElementList
        final ArrayList<ProcessingComponentDoc> algorithms;

        ProcessingComponentDocs(String suitePath) throws Exception
        {
            // I assume only classpath is scanned for the suite. We could add another
            // argument to specify the lookup path explicitly.
            final ResourceLookup resourceLookup = new ResourceLookup(
                new ContextClassLoaderLocator());

            final ProcessingComponentSuite suite = 
                ProcessingComponentSuite.deserialize(
                    resourceLookup.getFirst(suitePath), resourceLookup);

            final List<DocumentSourceDescriptor> sourceDescriptors = suite.getSources();
            this.sources = Lists.newArrayList();
            for (ProcessingComponentDescriptor descriptor : sourceDescriptors)
            {
                sources.add(new ProcessingComponentDoc(descriptor));
            }

            final List<ProcessingComponentDescriptor> algorithmDescriptors = suite
                .getAlgorithms();
            this.algorithms = Lists.newArrayList();
            for (ProcessingComponentDescriptor descriptor : algorithmDescriptors)
            {
                algorithms.add(new ProcessingComponentDoc(descriptor));
            }
            Collections.sort(algorithms, new Comparator<ProcessingComponentDoc>()
            {
                public int compare(ProcessingComponentDoc o1, ProcessingComponentDoc o2)
                {
                    return o1.componentDescriptor.getLabel().compareTo(
                        o2.componentDescriptor.getLabel());
                }
            });
        }
    }

    @Root(name = "processing-component-doc")
    static class ProcessingComponentDoc
    {
        @ElementList
        private final ArrayList<String> groups;

        @Element(name = "component-descriptor")
        private final ProcessingComponentDescriptor componentDescriptor;

        @ElementMap(entry = "attribute", name = "attribute-descriptors", inline = true, attribute = true, key = "key")
        private final HashMap<String, AttributeDescriptor> attributeDescriptors;

        @Element
        private final AttributeValueSets attributeSets;

        public ProcessingComponentDoc(ProcessingComponentDescriptor descriptor)
            throws InstantiationException, IllegalAccessException
        {
            this.componentDescriptor = descriptor;
            this.attributeSets = descriptor.getAttributeSets();

            // Instantiate the component and get bindable metadata
            BindableDescriptor bindableDescriptor = descriptor.getBindableDescriptor();
            this.attributeDescriptors = Maps.newHashMap(bindableDescriptor.attributeDescriptors);

            // Determine groups (it's easier to do that here than in XSLT)
            final HashSet<String> groupSet = Sets.newHashSet();
            for (AttributeDescriptor attributeDescriptor : attributeDescriptors.values())
            {
                final String groupName = attributeDescriptor.metadata.getGroup();
                if (groupName != null)
                {
                    groupSet.add(groupName);
                }
            }

            this.groups = Lists.newArrayList(groupSet);
            Collections.sort(this.groups);
        }
    }

    public static void main(String [] args) throws Exception
    {
        if (args.length != 2)
        {
            System.err.println("Args: suite-path output");
            System.exit(-1);
        }

        String suite = args[0];
        String output = args[1];

        try
        {
            final ProcessingComponentDocs components = new ProcessingComponentDocs(suite);
    
            final Format format = new Format(2,
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    
            new Persister(format).write(components,
                new FileOutputStream(new File(output)), "UTF-8");
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(-1);
        }
    }
}
