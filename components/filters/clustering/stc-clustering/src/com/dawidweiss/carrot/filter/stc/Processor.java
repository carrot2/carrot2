

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.filter.stc;


import com.dawidweiss.carrot.filter.stc.algorithm.*;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;


/**
 * SAX parser for Carrot2 search results data. Adds documents to processing while still parsing the
 * input.
 */
public class Processor
    extends org.xml.sax.helpers.DefaultHandler
{
    private static final Logger log = Logger.getLogger(Processor.class);
    private final Map stems = new HashMap();
    private final Set stopWords = new HashSet();
    private final List documents = new ArrayList();
    private Writer output;
    private boolean ignoreInput;

    public void setOutput(Writer w)
    {
        this.output = w;
    }


    private final void addToProcessing(Document document)
    {
        this.documents.add(document);
    }


    private final void finalProcessing()
        throws SAXException
    {
        log.debug("Start final processing.");

        // convert to DocReferences
        ArrayList list = new ArrayList(documents.size());

        for (Iterator i = documents.iterator(); i.hasNext();)
        {
            Document d = (Document) i.next();
            DocReference dr = new DocReference(
                    d.url, d.title,
                    (d.snippet == null) ? java.util.Collections.EMPTY_LIST
                                        : splitIntoSentences(d.snippet), d.snippet
                );
            list.add(dr);
        }

        STCEngine stcEngine = new STCEngine(list);
        stcEngine.stemSnippets(
            new ImmediateStemmer()
            {
                public String stemWord(String word)
                {
                    String x = (String) stems.get(word);

                    return ((x == null) ? word
                                        : x);
                }
            },
            new StopWordsDetector()
            {
                public boolean isStopWord(String word)
                {
                    return stopWords.contains(word);
                }
            }
        );

        stcEngine.createSuffixTree();
        stcEngine.createBaseClusters(2.0f, 1, 1.0f, 300, 2);

        /*
           List clusters = stcEngine.getBaseClusters();
           for (Iterator i = clusters.iterator(); i.hasNext(); )
           {
               BaseCluster b = (BaseCluster) i.next();
               System.out.println( b.getScore() + " >> " + b.getPhrase().userFriendlyTerms());
           }
         */
        stcEngine.createMergedClusters(0.6f);

        try
        {
            List clusters = stcEngine.getClusters();
            int max = 20;

            for (Iterator i = clusters.iterator(); i.hasNext() && (max > 0); max--)
            {
                Cluster b = (Cluster) i.next();

                // dump cluster group.
                Element group = new Element("group");
                Element title = new Element("title");
                group.addContent(title);

                List phrases = b.getPhrases();
                int maxPhr = 3;

                for (Iterator j = phrases.iterator(); j.hasNext() && (maxPhr > 0); maxPhr--)
                {
                    BaseCluster.Phrase p = (BaseCluster.Phrase) j.next();

                    Element phrase = new Element("phrase");
                    phrase.setText(p.userFriendlyTerms().trim());
                    title.addContent(phrase);
                }

                for (Iterator j = b.documents.iterator(); j.hasNext();)
                {
                    int docIndex = ((Integer) j.next()).intValue();

                    Element reference = new Element("document");
                    reference.setAttribute("refid", ((Document) documents.get(docIndex)).id);
                    group.addContent(reference);
                }

                new org.jdom.output.XMLOutputter("  ").output(group, output);
            }
        }
        catch (IOException e)
        {
            throw new SAXException("IOException: " + e.getMessage());
        }
        finally
        {
            log.debug("End final processing.");
        }
    }


    private final List splitIntoSentences(String snippet)
    {
        char [] chars = snippet.toLowerCase().toCharArray();
        List sentence = new ArrayList(10);
        List sentences = new ArrayList();

        int firstWordChar = 0;
        int current = 0;
        final int max = chars.length;

        // sentence boundary: ([.][\ \t\n\r])|([?!])+|([.][.][.])
        while (current < max)
        {
            switch (chars[current])
            {
                case '.':

                    if ((current - firstWordChar) >= 2)
                    {
                        sentence.add(new String(chars, firstWordChar, current - firstWordChar));
                    }

                    firstWordChar = current + 1;

                    break;

                case '\t':
                case ' ':
                case '\n':
                case '\r':

                    if ((current - firstWordChar) >= 2)
                    {
                        sentence.add(new String(chars, firstWordChar, current - firstWordChar));
                    }

                    firstWordChar = current + 1;

                    boolean stay = false;

                    if (current != 0)
                    {
                        if (sentence.size() > 0)
                        {
                            if (chars[current - 1] == '.')
                            {
                                if (
                                    ((current - 3) >= 0)
                                        && Character.isUpperCase(snippet.charAt(current - 2))
                                        && Character.isWhitespace(chars[current - 3])
                                )
                                {
                                    // it's possibly an abbreviation of the name
                                    // (Franklin D. Roosevelt)
                                }
                                else
                                {
                                    stay = true;
                                }
                            }
                        }
                    }

                    if (!stay)
                    {
                        break;
                    }

                case '?':
                case '!':

                    if ((current - firstWordChar) >= 2)
                    {
                        sentence.add(new String(chars, firstWordChar, current - firstWordChar));
                    }

                    firstWordChar = current + 1;

                    if (sentence.size() > 0)
                    {
                        sentences.add(new ArrayList(sentence));
                        sentence.clear();
                    }

                default:}

            current++;
        }

        if ((current - firstWordChar) >= 2)
        {
            sentence.add(new String(chars, firstWordChar, current - firstWordChar));
        }

        firstWordChar = current + 1;

        if (sentence.size() > 0)
        {
            sentences.add(new ArrayList(sentence));
            sentence.clear();
        }

        return sentences;
    }

    // variables used during XML parsing.
    private Document currentDocument;
    private int nestLevel;
    private int preserveCharacters;
    private static final int PRESERVE_TITLE = 1;
    private static final int PRESERVE_URL = 2;
    private static final int PRESERVE_SNIPPET = 3;
    private static final int PRESERVE_NONE = 4;

    // SAX2 callbacks
    public void startDocument()
        throws org.xml.sax.SAXException
    {
        log.debug("Start document.");
        this.preserveCharacters = PRESERVE_NONE;
        this.nestLevel = 0;
        this.ignoreInput = false;
    }


    public void endDocument()
        throws org.xml.sax.SAXException
    {
        try
        {
            log.debug("End document.");
            output.flush();
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }
    }


    public void ignorableWhitespace(char [] buf, int start, int length)
        throws org.xml.sax.SAXException
    {
        try
        {
            if (!ignoreInput)
            {
                output.write(buf, start, length);
            }
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }
    }


    public void characters(char [] buf, int start, int length)
        throws org.xml.sax.SAXException
    {
        try
        {
            if (!ignoreInput)
            {
                output.write(normalize(new String(buf, start, length)));
            }
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }

        switch (preserveCharacters)
        {
            case PRESERVE_SNIPPET:
                currentDocument.addSnippetChunk(buf, start, length);

                return;

            case PRESERVE_URL:
                currentDocument.addUrlChunk(buf, start, length);

                return;

            case PRESERVE_TITLE:
                currentDocument.addTitleChunk(buf, start, length);

                return;

            case PRESERVE_NONE:
                return;

            default:
                throw new RuntimeException(
                    "Illegal preserveCharacters state: " + preserveCharacters
                );
        }
    }


    public void startElement(String uri, String localName, String qname, Attributes attributes)
        throws org.xml.sax.SAXException
    {
        if (nestLevel == 0)
        {
            if (!"searchresult".equals(localName))
            {
                throw new SAXException("Root element must be 'searchresult'");
            }
        }
        else if (nestLevel == 1)
        {
            if ("document".equals(localName))
            {
                Document doc = new Document();

                doc.setId(attributes.getValue("", "id"));

                if (doc.id == null)
                {
                    throw new SAXException("id attribute is required in document definition");
                }

                this.currentDocument = doc;
            }
            else if ("group".equals(localName))
            {
                // strip any other group information
                this.ignoreInput = true;
            }
        }
        else if ((nestLevel == 2) && (currentDocument != null))
        {
            if ("title".equals(localName))
            {
                this.preserveCharacters = Processor.PRESERVE_TITLE;
            }
            else if ("url".equals(localName))
            {
                this.preserveCharacters = Processor.PRESERVE_URL;
            }
            else if ("snippet".equals(localName))
            {
                this.preserveCharacters = Processor.PRESERVE_SNIPPET;
            }
        }

        // recognize linguistic tags of the form: <l t="merced" s="merc" />
        // at any level of nesting.
        if ("l".equals(localName))
        {
            String term = attributes.getValue("", "t");
            String stem = attributes.getValue("", "s");

            if (term != null)
            {
                if (stem != null)
                {
                    if (stems.put(term, stem) != null)
                    {
                        throw new SAXException("Stem cannot be redefined for term: " + term);
                    }
                }

                if (attributes.getValue("", "sw") != null)
                {
                    stopWords.add(term);
                }
            }
        }

        // dump element start to output
        try
        {
            if (!ignoreInput)
            {
                output.write('<');
                output.write(qname);

                if (attributes != null)
                {
                    int len = attributes.getLength();

                    for (int i = 0; i < len; i++)
                    {
                        output.write(' ');
                        output.write(attributes.getQName(i));
                        output.write("=\"");
                        output.write(normalize(attributes.getValue(i)));
                        output.write('"');
                    }
                }

                output.write('>');
            }
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }

        this.nestLevel++;
    }


    public void endElement(String uri, String localName, String qname)
        throws org.xml.sax.SAXException
    {
        this.nestLevel--;

        if (nestLevel == 0)
        {
            // dump groups before closing the XML root.
            this.finalProcessing();
        }

        try
        {
            if (!ignoreInput)
            {
                output.write("</");
                output.write(qname);
                output.write('>');
            }
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }

        if (nestLevel == 1)
        {
            if ("document".equals(localName))
            {
                if (currentDocument.url == null)
                {
                    throw new SAXException("Document must contain an URL");
                }

                addToProcessing(currentDocument);
                this.currentDocument = null;
            }
            else if ("group".equals(localName))
            {
                this.ignoreInput = false;
            }
        }
        else if (nestLevel == 2)
        {
            if ("title".equals(localName) || "url".equals(localName) || "snippet".equals(localName))
            {
                this.preserveCharacters = Processor.PRESERVE_NONE;
            }
        }
    }


    public void fatalError(SAXParseException err)
        throws org.xml.sax.SAXException
    {
        log.error("Fatal error: ", err);
        throw err;
    }


    public void error(SAXParseException err)
        throws org.xml.sax.SAXException
    {
        log.error("Error: ", err);
        throw err;
    }


    /**
     * Normalizes the given string.
     */
    private final String normalize(String s)
    {
        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length()
                              : 0;

        for (int i = 0; i < len; i++)
        {
            char ch = s.charAt(i);

            switch (ch)
            {
                case '<':
                {
                    str.append("&lt;");

                    break;
                }

                case '>':
                {
                    str.append("&gt;");

                    break;
                }

                case '&':
                {
                    str.append("&amp;");

                    break;
                }

                case '"':
                {
                    str.append("&quot;");

                    break;
                }

                default:
                    str.append(ch);
            }
        }

        return str.toString();
    }


    public static void main(String [] args)
        throws Exception
    {
        File f = new File("F:\\Repositories\\ophelia\\carrot2\\test\\sample results\\logika.xml");
        Processor p = new Processor();
        SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
        XMLReader reader = parser.getXMLReader();

        System.err.println("Using: " + reader.getClass().getName());
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setErrorHandler(p);
        reader.setContentHandler(p);

        StringWriter sw = new StringWriter();
        p.setOutput(sw);
        reader.parse(new InputSource(new FileInputStream(f)));

        Writer x = new OutputStreamWriter(new FileOutputStream("os.xml"), "UTF-8");
        x.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        x.write(sw.toString());
        x.close();
    }
}
