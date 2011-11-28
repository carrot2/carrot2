
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

package org.carrot2.source.lucene;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.carrot2.util.ReflectionUtils;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.simpleframework.xml.core.Persister;

import com.carrotsearch.randomizedtesting.LifecycleScope;

/**
 * Test cases for {@link FSDirectoryWrapper}.
 */
public class FSDirectoryWrapperTest extends CarrotTestCase
{
    private static File indexDir;
    private static FSDirectory directory;

    @BeforeClass
    public static void installFSDirectoryWrapper() throws ClassNotFoundException
    {
        // Just load LuceneDocumentSource to install the wrapper
        ReflectionUtils.classForName(LuceneDocumentSource.class.getName());
    }

    @SuppressWarnings("deprecation")
    @BeforeClass
    public static void prepareIndex() throws Exception
    {
        indexDir = newTempDir(LifecycleScope.SUITE);
        directory = FSDirectory.open(indexDir);
        closeAfterSuite(directory);
        LuceneIndexUtils.createAndPopulateIndex(directory, 
            new SimpleAnalyzer(Version.LUCENE_CURRENT));
    }
    
    @Test
    public void testFSDirectorySerialization() throws Exception
    {
        FSDirectory unserializedDir = null;
        try
        {
            final File file = indexDir;
            final Persister persister = new Persister();

            final StringWriter writer = new StringWriter();
            persister.write(SimpleXmlWrappers.wrap(directory), writer);

            final SimpleXmlWrapperValue wrapper = persister.read(
                SimpleXmlWrapperValue.class, new StringReader(writer.toString()));
            assertThat(wrapper).describedAs("Wrapper for: " + writer.toString())
                .isNotNull();
            unserializedDir = SimpleXmlWrappers.unwrap(wrapper);

            assertThat(unserializedDir).isNotNull();
            assertThat(unserializedDir.getDirectory().getCanonicalFile())
                .isEqualTo(file.getCanonicalFile());
        }
        finally
        {
            if (unserializedDir != null)
            {
                unserializedDir.close();
            }
        }
    }
}
