
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Output;
import org.carrot2.util.resource.DirLocator;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import org.carrot2.shaded.guava.common.collect.ImmutableMap;

/**
 * Tests {@link ILexicalData}.
 */
public class DefaultLexicalDataFactoryTest extends CarrotTestCase
{
    /**
     * Binds basic preprocessing pipeline.
     */
    @Bindable
    public static class TestComponent extends ProcessingComponentBase
    {
        /**
         * Basic preprocessing pipeline.
         */
        public final BasicPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();

        /**
         * Expose the lexical data for English.
         */
        @Processing
        @Output
        @Attribute(key = "english")
        public ILexicalData english;

        @Override
        public void process() throws ProcessingException
        {
            english = preprocessingPipeline.lexicalDataFactory.getLexicalData(LanguageCode.ENGLISH);
        }
    }

    /**
     * Two controllers created with the same {@link DefaultLexicalDataFactory#resourceLookup}
     * should share parsed {@link ILexicalData}. 
     */
    @Test
    public void testLexicalDataFromTheSameResourceDirIsShared() throws IOException
    {
        final ILexicalData lexicalData1;
        final ILexicalData lexicalData2;

        // Use ctrl1
        {
            final Controller ctrl = ControllerFactory.createPooling();
            final ProcessingResult result = ctrl.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);

            lexicalData1 = result.getAttribute("english");
        }

        // Use ctrl1
        {
            final Controller ctrl = ControllerFactory.createPooling();
            final ProcessingResult result = ctrl.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);

            lexicalData2 = result.getAttribute("english");
        }

        assertSame(lexicalData1, lexicalData2);
    }

    /**
     * Lexical data from a given location can be reloaded on-demand. This affects all
     * pooled controllers, even if they have initialized earlier (lexical resources are
     * shared).
     */
    @Test
    public void testLexicalDataIsReloadedOnDemand() throws IOException
    {
        final File tempDir1 = newTempDir();
        FileUtils.writeStringToFile(new File(tempDir1, "stopwords.en"), "uniquea");

        final String resourceLookupKey = AttributeUtils.getKey(
            DefaultLexicalDataFactory.class, "resourceLookup");
        final String reloadResourcesKey = AttributeUtils.getKey(
            DefaultLexicalDataFactory.class, "reloadResources");

        final IResourceLocator classpathLocator = Location.CONTEXT_CLASS_LOADER.locator;

        // Create pooling controller, use tempDir1
        final Controller ctrl1 = ControllerFactory.createPooling();
        final ILexicalData data1;
        {
            ctrl1.init(ImmutableMap.<String, Object> of(
                resourceLookupKey, 
                new ResourceLookup(new DirLocator(tempDir1), classpathLocator)));

            final ProcessingResult result = ctrl1.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);

            data1 = result.getAttribute("english");
            assertTrue(data1.isCommonWord(new MutableCharArray("uniquea")));
        }

        // Create another pooling controller, same folder, but different resource lookup.
        final Controller ctrl2 = ControllerFactory.createPooling();
        final ILexicalData data2;
        {
            ctrl2.init(ImmutableMap.<String, Object> of(
                resourceLookupKey, 
                new ResourceLookup(new DirLocator(tempDir1), classpathLocator)));

            final ProcessingResult result = ctrl2.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);

            data2 = result.getAttribute("english");
            assertTrue(data2.isCommonWord(new MutableCharArray("uniquea")));

            assertSame(data1, data2);
        }

        /*
         * Now force reloading of resources from that path on ctrl1. The new stop word resource
         * should contain 'uniqueb'.
         */
        FileUtils.writeStringToFile(new File(tempDir1, "stopwords.en"), "uniqueb");

        final ILexicalData data3 = ctrl1.process(
            ImmutableMap.<String, Object> of(reloadResourcesKey, true), TestComponent.class)
                .getAttribute("english");

        assertNotSame(data1, data3);
        assertFalse(data3.isCommonWord(new MutableCharArray("uniquea")));
        assertTrue(data3.isCommonWord(new MutableCharArray("uniqueb")));

        /*
         * But since it's the same location, all other controllers should now see updated resources
         * (and share the same lexical data).
         */
        final ILexicalData data4 = ctrl2.process(
            Collections.<String, Object> emptyMap(), TestComponent.class).getAttribute("english");

        assertSame(data3, data4);
    }

    /**
     * Two controllers with different {@link DefaultLexicalDataFactory#resourceLookup}
     * should not affect each other's resources. 
     */
    @Test
    public void testSeparateLexicalDataForDifferentResourceLookup() throws IOException
    {
        final File tempDir1 = newTempDir();
        FileUtils.writeStringToFile(new File(tempDir1, "stopwords.en"), "uniquea");

        final File tempDir2 = newTempDir();
        FileUtils.writeStringToFile(new File(tempDir2, "stopwords.en"), "uniqueb");

        final IResourceLocator classpathLocator = Location.CONTEXT_CLASS_LOADER.locator;

        final String resourceLookupKey = AttributeUtils.getKey(DefaultLexicalDataFactory.class, "resourceLookup");
        final String resourceReloadKey = AttributeUtils.getKey(DefaultLexicalDataFactory.class, "reloadResources");

        // Create pooling controller, use tempDir1
        final Controller ctrl1 = ControllerFactory.createPooling();
        {
            ctrl1.init(ImmutableMap.<String, Object> of(
                resourceLookupKey, 
                new ResourceLookup(new DirLocator(tempDir1.getPath()), classpathLocator),
                resourceReloadKey,
                true));
    
            final ProcessingResult result = ctrl1.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);
            final ILexicalData data = result.getAttribute("english");

            assertTrue(data.isCommonWord(new MutableCharArray("uniquea")));
            assertFalse(data.isCommonWord(new MutableCharArray("uniqueb")));
        }

        // Create pooling controller, use tempDir2
        final Controller ctrl2 = ControllerFactory.createPooling();
        {
            ctrl2.init(ImmutableMap.<String, Object> of(resourceLookupKey, 
                new ResourceLookup(new DirLocator(tempDir2.getPath()), classpathLocator)));
    
            final ProcessingResult result = ctrl2.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);
            final ILexicalData data = result.getAttribute("english");

            assertFalse(data.isCommonWord(new MutableCharArray("uniquea")));
            assertTrue(data.isCommonWord(new MutableCharArray("uniqueb")));
        }

        // Now, reuse the first controller, nothing should change.
        {
            final ProcessingResult result = ctrl1.process(
                Collections.<String, Object> emptyMap(), TestComponent.class);
            final ILexicalData data = result.getAttribute("english");

            assertTrue(data.isCommonWord(new MutableCharArray("uniquea")));
            assertFalse(data.isCommonWord(new MutableCharArray("uniqueb")));
        }        
    }
}
