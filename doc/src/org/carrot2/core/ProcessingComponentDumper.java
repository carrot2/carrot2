package org.carrot2.core;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptorBuilder;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import com.google.common.collect.*;

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
            final ProcessingComponentSuite suite = ProcessingComponentSuite
                .deserialize(ResourceUtilsFactory.getDefaultResourceUtils().getFirst(
                    suitePath));

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

    @SuppressWarnings("unused")
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
        String suite = "suites/suite-doc.xml";
        String output = null;

        if (args.length == 2)
        {
            suite = args[0];
            output = args[1];
        }

        final ProcessingComponentDocs components = new ProcessingComponentDocs(suite);

        final Format format = new Format(2,
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (output == null)
        {
            new Persister(format).write(components, System.out);
        }
        else
        {
            new Persister(format).write(components,
                new FileOutputStream(new File(output)), "UTF-8");
        }
    }
}
