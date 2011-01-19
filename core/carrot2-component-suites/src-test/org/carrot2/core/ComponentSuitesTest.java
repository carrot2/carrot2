
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.util.resource.ClassLocator;
import org.carrot2.util.resource.ContextClassLoaderLocator;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceLookup;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * A very rough test case for the deserialization of component suites.
 */
public class ComponentSuitesTest
{
    @Test
    public void testCarrot2DefaultComponentSuite() throws Exception
    {
        // Default suites should be reachable via context class loader.
        ResourceLookup resourceLookup = new ResourceLookup(
            new ContextClassLoaderLocator());

        checkSuite("suite-dcs.xml", resourceLookup);
        checkSuite("suite-doc.xml", resourceLookup);
        checkSuite("suite-workbench.xml", resourceLookup);
    }

    @Test
    public void testIncludes() throws Exception
    {
        // Test suite should be reachable via this class's class loader.
        ResourceLookup resourceLookup = new ResourceLookup(
            new ClassLocator(this.getClass()));

        IResource resource = resourceLookup.getFirst("suite-including.xml");
        assertThat(resource).isNotNull();
        ProcessingComponentSuite suite = ProcessingComponentSuite.deserialize(resource,
            resourceLookup);

        assertThat(suite).isNotNull();

        final Function<ProcessingComponentDescriptor, String> toIdTransformer = new Function<ProcessingComponentDescriptor, String>()
        {
            public String apply(ProcessingComponentDescriptor descriptor)
            {
                return descriptor.getId();
            }
        };
        assertThat(Lists.transform(suite.getSources(), toIdTransformer)).containsOnly(
            "including-beginning", "included-beginning", "including-middle",
            "included-middle", "included-end", "including-end");
        assertThat(Lists.transform(suite.getAlgorithms(), toIdTransformer)).containsOnly(
            "included-beginning", "included-beginning-2", "including-middle",
            "including-middle-2", "included-end", "including-end");
        assertThat(suite.includes).isNull();
    }

    private void checkSuite(final String suitePath, ResourceLookup resourceLookup)
        throws Exception
    {
        IResource resource = resourceLookup.getFirst(suitePath);
        assertThat(resource).isNotNull();
        ProcessingComponentSuite suite = ProcessingComponentSuite.deserialize(resource,
            resourceLookup);

        assertThat(suite).isNotNull();
        assertThat(suite.getAlgorithms()).isNotEmpty();
        assertThat(suite.getSources()).isNotEmpty();

        for (DocumentSourceDescriptor source : suite.getSources())
        {
            assertThat(source.getId()).isNotEmpty();
            assertThat(source.getAttributeSets()).isNotNull();
            assertThat(source.getLabel()).isNotEmpty();
            assertThat(source.getTitle()).isNotEmpty();
        }

        for (ProcessingComponentDescriptor algorithm : suite.getAlgorithms())
        {
            assertThat(algorithm.getId()).isNotEmpty();
            assertThat(algorithm.getAttributeSets()).isNotNull();
        }
    }
}
