

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


package com.dawidweiss.carrot.filter.textonly;


import com.dawidweiss.carrot.tokenizer.Tokenizer;
import org.apache.log4j.Logger;
import org.xml.sax.*;
import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;


/**
 * SAX content handler for parsing snippets and returning only proper tokens (according to what
 * Tokenizer class claims is a proper token).
 */
public class SAXHandler
    extends org.xml.sax.helpers.DefaultHandler
{
    private static final Logger log = Logger.getLogger(SAXHandler.class);
    private static final Tokenizer tokenizer = Tokenizer.getTokenizer();
    private Writer output;

    public void setOutput(Writer w)
    {
        this.output = w;
    }

    // variables used during XML parsing.
    private StringBuffer buffer;
    private ArrayList stack;
    private boolean preserveCharacters;

    // SAX2 callbacks
    public void startDocument()
        throws org.xml.sax.SAXException
    {
        this.buffer = new StringBuffer();
        this.stack = new ArrayList();
        this.preserveCharacters = false;
    }


    public void endDocument()
        throws org.xml.sax.SAXException
    {
        try
        {
            log.debug("End document.");
            output.flush();
            output.close();
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
            output.write(buf, start, length);
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
            if (preserveCharacters)
            {
                this.buffer.append(new String(buf, start, length));
            }
            else
            {
                output.write(normalize(new String(buf, start, length)));
            }
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }
    }


    public void startElement(String uri, String localName, String qname, Attributes attributes)
        throws org.xml.sax.SAXException
    {
        stack.add(localName);

        if (stack.size() == 1)
        {
            if (!"searchresult".equals(localName))
            {
                throw new SAXException("Root element must be 'searchresult'");
            }
        }
        else if ((stack.size() == 3) && stack.get(1).equals("document"))
        {
            if ("title".equals(localName) || "snippet".equals(localName))
            {
                this.preserveCharacters = true;
            }
        }

        // dump element start to output
        try
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
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }
    }


    public void endElement(String uri, String localName, String qname)
        throws org.xml.sax.SAXException
    {
        stack.remove(stack.size() - 1);

        if (this.preserveCharacters)
        {
            try
            {
                StringBuffer buf = new StringBuffer(buffer.length());

                synchronized (tokenizer)
                {
                    tokenizer.restartTokenizerOn(buffer.toString());

                    final int [] type = { 0 };
                    String s;

                    while ((s = tokenizer.getNextToken(type)) != null)
                    {
                        switch (type[0])
                        {
                            case Tokenizer.TYPE_EMAIL:
                            case Tokenizer.TYPE_TERM:
                            case Tokenizer.TYPE_URL:
                            case Tokenizer.TYPE_PERSON:

                                if (buf.length() > 0)
                                {
                                    buf.append(' ');
                                }

                                buf.append(s);

                                break;

                            case Tokenizer.TYPE_SENTENCEMARKER:
                                buf.append(s);

                                break;

                            default:
                                log.warn("Unrecognized token type: " + type[0]);
                                buf.append(s);
                        }
                    }
                }

                output.write(normalize(buf.toString()));
            }
            catch (IOException e)
            {
                throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
            }
            finally
            {
                this.buffer.setLength(0);
                preserveCharacters = false;
            }
        }

        try
        {
            output.write("</");
            output.write(qname);
            output.write('>');
        }
        catch (IOException e)
        {
            throw new org.xml.sax.SAXException("IOException: " + e.getMessage());
        }
    }


    public void fatalError(SAXParseException err)
        throws org.xml.sax.SAXException
    {
        System.out.println("Fatal error: " + err);
    }


    public void error(SAXParseException err)
        throws org.xml.sax.SAXException
    {
        System.out.println("Error: " + err);
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

                case '\r':
                case '\n':
                {
                    //str.append("&#");
                    //str.append(Integer.toString(ch));
                    //str.append(';');
                    //break;
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
        SAXHandler p = new SAXHandler();
        SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
        XMLReader reader = parser.getXMLReader();

        System.err.println("Using: " + reader.getClass().getName());
        reader.setFeature("http://xml.org/sax/features/validation", false);
        reader.setFeature("http://xml.org/sax/features/namespaces", true);
        reader.setErrorHandler(p);
        reader.setContentHandler(p);

        org.put.util.time.ElapsedTimeTimer timer = new org.put.util.time.ElapsedTimeTimer();

        StringWriter sw = new StringWriter();
        p.setOutput(sw);
        reader.parse(new InputSource(new FileInputStream(f)));

        Writer x = new OutputStreamWriter(new FileOutputStream("os.xml"), "UTF-8");
        x.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        x.write(sw.toString());
        x.close();

        System.err.print(timer.toString());
    }
}
