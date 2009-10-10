package org.carrot2.source.lucene;

import static org.fest.assertions.Assertions.assertThat;

import java.io.*;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.carrot2.util.simplexml.SimpleXmlWrapperValue;
import org.carrot2.util.simplexml.SimpleXmlWrappers;
import org.junit.*;
import org.simpleframework.xml.load.Persister;

/**
 * Test cases for {@link FSDirectoryWrapper}.
 */
public class FSDirectoryWrapperTest
{
    private static File indexDir;
    private static FSDirectory directory;

    @BeforeClass
    public static void installFSDirectoryWrapper() throws ClassNotFoundException
    {
        // Just load LuceneDocumentSource to install the wrapper
        @SuppressWarnings("unused")
        final LuceneDocumentSource unused = new LuceneDocumentSource();
    }

    @BeforeClass
    public static void prepareIndex() throws Exception
    {
        indexDir = new File(new File(System.getProperty("java.io.tmpdir"))
            .getCanonicalPath(), "index" + Math.abs(new Random().nextInt()));
        directory = FSDirectory.open(indexDir);
        LuceneIndexUtils.createAndPopulateIndex(directory, new SimpleAnalyzer());
    }

    @AfterClass
    public static void removeIndex()
    {
        try
        {
            directory.close();
        }
        finally
        {
            try
            {
                FileUtils.deleteDirectory(indexDir);
            }
            catch (IOException ignored)
            {
            }
        }
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
            unserializedDir = SimpleXmlWrappers.unwrap(wrapper);

            assertThat(unserializedDir).isNotNull();
            assertThat(unserializedDir.getFile()).isEqualTo(file);
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
