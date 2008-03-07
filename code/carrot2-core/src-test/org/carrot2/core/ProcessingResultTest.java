package org.carrot2.core;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.carrot2.util.reflect.ObjectEquivalenceHelper.wrap;

import java.io.*;
import java.util.List;
import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.TestDocumentFactory;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.CollectionUtils;
import org.junit.Test;

import com.google.common.collect.Maps;

public class ProcessingResultTest
{
    @Test
    public void testDocumentSerializationDeserialization() throws Exception
    {
        final List<Document> documents = TestDocumentFactory.DEFAULT.generate(5);
        final Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(AttributeNames.DOCUMENTS, documents);

        final ProcessingResult sourceProcessingResult = new ProcessingResult(attributes);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sourceProcessingResult.serialize(outputStream);
        CloseableUtils.close(outputStream);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream
            .toByteArray());

        final ProcessingResult deserialized = ProcessingResult.deserialize(inputStream);

        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());
        assertEquals(wrap(documents), wrap(deserialized.getDocuments()));
    }

    @Test
    public void testDocumentDeserializationFromLegacyXml() throws Exception
    {
        final String query = "apple computer";
        final String title = "Apple Computer, Inc.";
        final String snippet = "Macintosh hardware, software, and Internet tools.";
        final String url = "http:// www.apple.com/";

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<searchresult>\n");
        xml.append("<query>" + query + "</query>\n");
        xml.append("<document id=\"0\">");
        xml.append("<title>" + title + "</title>\n");
        xml.append("<snippet>" + snippet + "</snippet>\n");
        xml.append("<url>" + url + "</url>\n");
        xml.append("</document>\n");
        xml.append("</searchresult>\n");

        final StringReader reader = new StringReader(xml.toString());

        final ProcessingResult deserialized = ProcessingResult.deserialize(reader);

        assertNotNull(deserialized);
        assertNotNull(deserialized.getAttributes());

        Document deserializedDocument = CollectionUtils.getFirst(deserialized
            .getDocuments());
        assertEquals(title, deserializedDocument.getField(Document.TITLE));
        assertEquals(snippet, deserializedDocument.getField(Document.SUMMARY));
        assertEquals(url, deserializedDocument.getField(Document.CONTENT_URL));
    }
}
