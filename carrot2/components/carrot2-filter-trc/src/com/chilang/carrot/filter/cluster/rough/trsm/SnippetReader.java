/**
 * 
 * @author chilang
 * Created 2003-07-17, 22:44:57.
 */
package com.chilang.carrot.filter.cluster.rough.trsm;

import com.chilang.carrot.filter.cluster.rough.data.SnippetDocument;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class SnippetReader extends org.xml.sax.helpers.DefaultHandler {
    private static final int EMPTY_NODE = 0;
    private static final int DOCUMENT_NODE = 1;
    private static final int TITLE_NODE = 2;
    private static final int URL_NODE = 3;
    private static final int SNIPPET_NODE = 4;
    private static final int GROUP_NODE = 5;
    private static final int QUERY_NODE = 6;

    private int nodeType = EMPTY_NODE;
    private SnippetDocument currentDocument;

    private StringBuffer buffer;

    private boolean bufferData = false;

    private Collection snippets;

    private String query;

    public String getQuery() {
        return query;
    }

    public SnippetReader(String filename) {
        snippets = new ArrayList();
        readFromFile(filename);
    }

    public void endDocument() throws SAXException {
//        System.out.println("Start document");
    }

    public void startDocument() throws SAXException {
//        System.out.println("Start document");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//        System.out.println("uri:"+uri+",local:"+localName+":"+"qname:"+qName+",attr:"+attributes.getLength());
        if ("query".equals(qName)) {
            nodeType = QUERY_NODE;
        } else if ("group".equals(qName)) {
            nodeType = GROUP_NODE;
        } else if ("document".equals(qName)) {
            String id = attributes.getValue("id");
            currentDocument = new SnippetDocument(id);
            nodeType = DOCUMENT_NODE;
        } else if ("title".equals(qName)) {
            nodeType = TITLE_NODE;
        } else if ("url".equals(qName)) {
            nodeType = URL_NODE;
        } else if ("snippet".equals(qName)) {
            nodeType = SNIPPET_NODE;
            buffer = new StringBuffer();
        }
    }

    public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
        System.out.println("ws[" + (length - start) + "]=" + new String(ch, start, length));
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //exit from group node
        if ("group".equals(qName) || "query".equals(qName)) {
            nodeType = EMPTY_NODE;
        }
        if ((nodeType != GROUP_NODE) && (nodeType != QUERY_NODE)) {
            if ("title".equals(qName)) {
                currentDocument.setTitle(buffer.toString());
            } else if ("url".equals(qName)) {
                currentDocument.setUrl(buffer.toString());
            } else if ("snippet".equals(qName)) {
                currentDocument.setDescription(buffer.toString());
            } else if ("document".equals(qName)) {
                snippets.add(currentDocument);
//            System.out.println(currentDocument);
            }
        }
        bufferData = false;
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        String s = new String(ch, start, length);
//        System.out.println("node="+nodeType);
//        System.out.println("s("+s.length()+")="+s);

//        System.out.println(nodeType+"=("+s.trim().length()+")"+s.trim());

        if (s.trim().length() == 0)
            return;

        switch (nodeType) {

            case QUERY_NODE:
                query = s;
            case TITLE_NODE:
//                currentDocument.setTitle(s);
//                return;
            case URL_NODE:
//                currentDocument.setUrl(s);
//                return;
            case SNIPPET_NODE:
                if (bufferData)
                    buffer.append(s);
                else {
                    buffer = new StringBuffer(s);
                    bufferData = true;
                }
//                currentDocument.setDescription(s);

                return;
            default:
                return;
        }

    }

    public void fatalError(SAXParseException err)
            throws org.xml.sax.SAXException {
        System.out.println("Fatal error: " + err);
        throw err;
    }


    public void error(SAXParseException err)
            throws org.xml.sax.SAXException {
        System.out.println("Error: " + err);
        throw err;
    }

    public Collection getSnippets() {
        return snippets;
    }

    public void readFromFile(String filename) {
        XMLReader reader = null;
        try {
            SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
            reader = parser.getXMLReader();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError factoryConfigurationError) {
            factoryConfigurationError.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }


//        System.err.println("Using: " + reader.getClass().getName());
//        reader.setFeature("http://xml.org/sax/features/validation", false);
//        reader.setFeature("http://xml.org/sax/features/namespaces", false);
        reader.setErrorHandler(this);
        reader.setContentHandler(this);

        try {
            reader.parse(filename);
        } catch (IOException e) {
            System.out.println("Error : " + e);
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (SAXException e) {
            System.out.println("Error : " + e);
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

/*for (Iterator iter = getSnippets().iterator(); iter.hasNext();) {
System.out.println((SnippetDocument)iter.next());
}*/
    }

    public static void main(String argv[]) {
        SnippetReader snippetReader = new SnippetReader(argv[0]);
        System.out.println("Query : "+snippetReader.getQuery());
        System.out.println("Size  : "+snippetReader.getSnippets().size());

//        TermExtractor extractor = new SimpleTermExtractor(new PorterStemmer(),
//                        new StopWordsSet("C:\\soft\\java\\jakarta-tomcat-4.1.18-LE-jdk14\\webapps\\rough\\WEB-INF/stopwords/stopwords-en.txt"));
//        for (Iterator iterator = snippetReader.getSnippets().iterator(); iterator.hasNext();) {
//            Snippet snippet = (Snippet) iterator.next();
//            Collection terms = (Collection)extractor.extractFromSnippet((Document)snippet);
//            System.out.println(terms);
//        }
    }
}
