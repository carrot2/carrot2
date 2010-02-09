
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.ProcessingComponentDescriptor.Position;
import org.carrot2.util.resource.*;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * A very rough test case for the component suites defined in this project.
 */
public class ComponentSuitesTest
{
    private ResourceUtils resourceUtils = ResourceUtilsFactory.getDefaultResourceUtils();

    @Test
    public void testCarrot2DefaultComponentSuite() throws Exception
    {
        checkSuite("/suites/suite-dcs.xml");
        checkSuite("/suites/suite-doc.xml");
        checkSuite("/suites/suite-workbench.xml");
    }

    private void checkSuite(final String suitePath) throws Exception
    {
        IResource resource = resourceUtils.getFirst(suitePath, ComponentSuitesTest.class);
        assertThat(resource).isNotNull();
        ProcessingComponentSuite suite = ProcessingComponentSuite.deserialize(resource);

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

    @Test
    public void testIncludes() throws Exception
    {
        IResource resource = resourceUtils.getFirst("/suite-including.xml",
            ComponentSuitesTest.class);
        assertThat(resource).isNotNull();
        ProcessingComponentSuite suite = ProcessingComponentSuite.deserialize(resource);

        assertThat(suite).isNotNull();

        final Function<ProcessingComponentDescriptor, String> toIdTransformer = new Function<ProcessingComponentDescriptor, String>()
        {
            public String apply(ProcessingComponentDescriptor descriptor)
            {
                return descriptor.getId();
            }
        };
        final Function<ProcessingComponentDescriptor, Position> toPositionTransformer = new Function<ProcessingComponentDescriptor, Position>()
        {
            public Position apply(ProcessingComponentDescriptor descriptor)
            {
                return descriptor.position;
            }
        };
        assertThat(Lists.transform(suite.getSources(), toIdTransformer)).containsOnly(
            "including-beginning", "included-beginning", "including-middle",
            "included-middle", "included-end", "including-end");
        assertThat(Lists.transform(suite.getAlgorithms(), toIdTransformer)).containsOnly(
            "included-beginning", "included-beginning-2", "including-middle",
            "including-middle-2", "included-end", "including-end");
        assertThat(Lists.transform(suite.getSources(), toPositionTransformer))
            .containsOnly(null, null, null, null, null, null);
        assertThat(Lists.transform(suite.getAlgorithms(), toPositionTransformer))
            .containsOnly(null, null, null, null, null, null);
        assertThat(suite.includes).isNull();
    }
}
