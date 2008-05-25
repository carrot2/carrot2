package org.carrot2.core.suite;

import static junit.framework.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.*;
import org.carrot2.util.resource.*;
import org.junit.Test;

/**
 * A very rough test case for the component suites defined in this project.
 */
public class ComponentSuitesTest
{
    private ResourceUtils resourceUtils = ResourceUtilsFactory.getDefaultResourceUtils();

    @Test
    public void testCarrot2DefaultComponentSuite() throws Exception
    {
        Resource resource = resourceUtils.getFirst("/carrot2-default/suite.xml",
            ComponentSuitesTest.class);
        assertNotNull(resource);
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

            if ("yahoo-news".equals(source.getId()))
            {
                assertThat(source.getAttributeSets().getAttributeValueSetIds())
                    .isNotEmpty();
            }
        }

        for (ProcessingComponentDescriptor algorithm : suite.getAlgorithms())
        {
            assertThat(algorithm.getId()).isNotEmpty();
            assertThat(algorithm.getAttributeSets()).isNotNull();
        }
    }
}
