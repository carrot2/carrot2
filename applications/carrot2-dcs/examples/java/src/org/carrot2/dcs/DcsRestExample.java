package org.carrot2.dcs;

import java.io.*;
import java.util.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.*;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

/**
 * An example of calling Document Clustering Server service in Java. Please see
 * http://localhost:8080 for a list of all available parameters.
 */
public class DcsRestExample
{
    /**
     * The URL at which the DCS is available.
     */
    private static final String DCS_URL = "http://localhost:8080/dcs/rest";

    public static void main(String [] args) throws Exception
    {
        final Map<String, String> attributes = new LinkedHashMap<String, String>();

        // Clustering input in Carrot2 XML format read from a local file / input stream.
        System.out.println("## Clustering documents from a local file");
        // Optionally, we can pass the query that generated the documents
        // to avoid creation of trivial clusters.
        attributes.put("query", "data mining");
        // Note that the dcs.c2stream parameter must come last
        attributes.put("dcs.c2stream", new String(
            readFullyAndClose(new InputStreamReader(new FileInputStream(
                "input/data-mining.xml"), "UTF-8"))));
        displayResults(sendDcsPostRequest(attributes));

        // Clustering input from a remote XML stream,
        System.out.println("## Clustering documents from a remote XML feed");
        attributes.clear();
        attributes.put("dcs.source", "xml");
        attributes.put("dcs.algorithm", "stc");
        attributes
            .put("XmlDocumentSource.xml",
                "http://search.carrot2.org/stable/xml?source=web&type=CARROT2&q=test&results=20");
        displayResults(sendDcsPostRequest(attributes));

        // Clustering results fetched from a search engine.
        // For this request, we will pass some additional attributes to the default
        // algorithm and require the DCS not to repeat the fetched documents on the
        // output.
        System.out.println("## Clustering search results from a search engine");
        attributes.clear();
        attributes.put("dcs.source", "etools");
        attributes.put("query", "test");
        attributes.put("results", "20");
        attributes.put("dcs.algorithm", "lingo");
        attributes.put("LingoClusteringAlgorithm.desiredClusterCountBase", "10");
        attributes.put("LingoClusteringAlgorithm.factorizationQuality", "LOW");
        attributes.put("LingoClusteringAlgorithm.factorizationFactory",
            "org.carrot2.matrix.factorization.PartialSingularValueDecompositionFactory");
        displayResults(sendDcsPostRequest(attributes));
    }

    /**
     * Sends a request to the DCS. Please note that this method does not performance any
     * error checking.
     * 
     * @param attributes parameters to send to the DCS
     * @return raw response.
     */
    private static InputStream sendDcsPostRequest(Map<String, String> attributes)
        throws Exception
    {
        final HttpClient client = new HttpClient();
        final PostMethod post = new PostMethod(DCS_URL);
        final List<Part> parts = new ArrayList<Part>();
        for (Map.Entry<String, String> entry : attributes.entrySet())
        {
            parts.add(new StringPart(entry.getKey(), entry.getValue()));
        }
        post.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part [parts
            .size()]), post.getParams()));
        client.executeMethod(post);
        return post.getResponseBodyAsStream();
    }

    /**
     * Simple parsing and display of the response. This method uses dom4j for parsing XML,
     * feel free to use anything that comes handy.
     */
    @SuppressWarnings("unchecked")
    private static void displayResults(InputStream results) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(results);
        System.out.println("Cluster labels:");
        for (Iterator<Element> it = document.getRootElement().elementIterator("group"); it
            .hasNext();)
        {
            System.out.println(it.next().element("title").elementText("phrase"));
        }
        System.out.println();
    }

    /**
     * Reads all the input stream, closes the stream and returns the content.
     */
    private static char [] readFullyAndClose(final Reader input) throws IOException
    {
        try
        {
            final CharArrayWriter baos = new CharArrayWriter(8 * 1024);
            final char [] buffer = new char [8 * 1024];

            int z;
            while ((z = input.read(buffer)) > 0)
            {
                baos.write(buffer, 0, z);
            }

            return baos.toCharArray();
        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
    }
}
