

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


package org.put.snippetreader.readers;


import org.apache.log4j.Logger;
import org.jdom.Element;
import org.put.snippetreader.extractors.regexp.*;
import org.put.snippetreader.readers.HtmlMultipage.HttpMultiPageReader;
import org.put.util.exception.ExceptionHelper;
import org.put.util.io.FileHelper;
import org.put.util.net.http.*;
import org.put.util.text.HtmlHelper;
import org.put.util.xml.JDOMHelper;
import gnu.regexp.RE;
import java.io.*;
import java.util.Vector;


/**
 * Reads snippets from a Web search engine.
 */
public class WebSnippetReader
{
    private static final Logger log = Logger.getLogger(WebSnippetReader.class);
    Element config;
    HttpMultiPageReader reader;
    RegExpSnippetExtractor extractor;

    /**
     * Initializes this snippet reader to use some service. The configuration is a JDOM XML
     * structure.
     */
    public WebSnippetReader(Element configuration)
        throws Exception
    {
        config = configuration;

        FormActionInfo actionInfo = new FormActionInfo(
                JDOMHelper.getElement("/service/request", config)
            );
        FormParameters queryParameters = new FormParameters(
                JDOMHelper.getElement("/service/request/parameters", config)
            );
        HTTPFormSubmitter submitter = new HTTPFormSubmitter(actionInfo);

        reader = new HttpMultiPageReader(submitter, queryParameters);
        extractor = new RegExpSnippetExtractor(
                new SnippetDescription(JDOMHelper.getElement("/service/response/snippet", config))
            );
    }

    /**
     * Writes a Carrot2 XML stream to the given Writer.
     */
    public void getSnippetsAsCarrot2XML(
        final Writer outputStream, String query, final int snippetsNeeded
    )
        throws Exception
    {
        outputStream.write("<searchresult>\n");

        // output the optional query tag at the beginning of the document.
        outputStream.write(
            "<query requested-results=\"" + snippetsNeeded + "\"><![CDATA[" + query
            + "]]></query>\n"
        );

        try
        {
            String encoding;

            if (
                (encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false
                        )) == null
            )
            {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(
                    query, snippetsNeeded, encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config)
                );

            if (is != null)
            {
                final float warnLevel = Float.parseFloat(
                        JDOMHelper.getStringFromJDOM(
                            "/service/response/pageinfo/warn-when-below", config, true
                        )
                    );

                extractor.extractSnippets(
                    new InputStreamReader(
                        is,
                        JDOMHelper.getStringFromJDOM("/service/response#encoding", config, false)
                    ),
                    new SnippetExtractorCallback()
                    {
                        int notitle = 0;
                        int nourl = 0;
                        int nosummary = 0;
                        int recognized = 0;

                        public void snippetHasNoTitle()
                        {
                            notitle++;
                        }


                        public void snippetHasNoURL()
                        {
                            nourl++;
                        }


                        public boolean acceptSnippetWithEmptySummary()
                        {
                            nosummary++;

                            return true;
                        }


                        public void snippetRecognized(SimpleSnippet s)
                        {
                            if (s == null)
                            {
                                if (recognized < (snippetsNeeded * warnLevel))
                                {
                                    // Issue a warning.
                                    log.warn(
                                        "Only " + recognized + " out of " + snippetsNeeded
                                        + " results were extracted."
                                    );
                                }
                            }
                            else
                            {
                                try
                                {
                                    recognized++;
                                    outputStream.write(
                                        "<document id=\"" + recognized + "\">\n\t<title>"
                                    );
                                    outputStream.write(
                                        xmlencode(HtmlHelper.removeHtmlTags(s.getTitle()))
                                    );
                                    outputStream.write("</title>\n");

                                    outputStream.write("\t<url>");
                                    outputStream.write(xmlencode(s.getDocumentURL()));
                                    outputStream.write("</url>\n");

                                    if (s.getSummary() != null)
                                    {
                                        outputStream.write("\t<snippet>");
                                        outputStream.write(
                                            xmlencode(HtmlHelper.removeHtmlTags(s.getSummary()))
                                        );
                                        outputStream.write("</snippet>\n");
                                    }

                                    outputStream.write("</document>\n");
                                }
                                catch (IOException e)
                                {
                                    throw new RuntimeException("IOException when saving result.");
                                }
                            }
                        }


                        private String xmlencode(String x)
                        {
                            return "<![CDATA[" + x + "]]>";
                        }
                    }
                );
                is.close();
            }

            outputStream.write("</searchresult>\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Retrieves snippets for a query.
     */
    public Vector getSnippets(String query, final int snippetsNeeded)
        throws Exception
    {
        final Vector res = new Vector();

        try
        {
            String encoding;

            if (
                (encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false
                        )) == null
            )
            {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(
                    query, snippetsNeeded, encoding,
                    JDOMHelper.getElement("/service/response/pageinfo", config)
                );

            if (is == null)
            {
                return res;
            }

            final float warnLevel = Float.parseFloat(
                    JDOMHelper.getStringFromJDOM(
                        "/service/response/pageinfo/warn-when-below", config, true
                    )
                );

            extractor.extractSnippets(
                new InputStreamReader(
                    is, JDOMHelper.getStringFromJDOM("/service/response#encoding", config, false)
                ),
                new SnippetExtractorCallback()
                {
                    int notitle = 0;
                    int nourl = 0;
                    int nosummary = 0;
                    int recognized = 0;

                    public void snippetHasNoTitle()
                    {
                        notitle++;
                    }


                    public void snippetHasNoURL()
                    {
                        nourl++;
                    }


                    public boolean acceptSnippetWithEmptySummary()
                    {
                        nosummary++;

                        return true;
                    }


                    public void snippetRecognized(SimpleSnippet s)
                    {
                        if (s == null)
                        {
                            if (recognized < (snippetsNeeded * warnLevel))
                            {
                                // Issue a warning.
                                log.warn(
                                    "Only " + recognized + " out of " + snippetsNeeded
                                    + " results were extracted."
                                );
                            }
                        }
                        else
                        {
                            recognized++;
                            res.add(HtmlHelper.removeHtmlTags(s.getTitle()));
                            res.add(s.getDocumentURL());
                            res.add(
                                (s.getSummary() == null) ? ""
                                                         : HtmlHelper.removeHtmlTags(
                                    s.getSummary()
                                )
                            );
                        }
                    }
                }
            );

            is.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }

        return res;
    }


    /**
     * Retrieves a HTML source of a query, highlights matches.
     */
    public String getHighlightedMatches(String query, int snippetsNeeded)
        throws Exception
    {
        final StringBuffer tokenizedStream = new StringBuffer();

        try
        {
            String encoding;

            if (
                (encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false
                        )) == null
            )
            {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(
                    query, 80, encoding, JDOMHelper.getElement(
                        "/service/response/pageinfo", config
                    )
                );

            final byte [] fullInput = FileHelper.readFullyAndCloseInput(is);
            final String fullInputString = new String(
                    fullInput,
                    JDOMHelper.getStringFromJDOM("/service/response#encoding", config, false)
                );

            extractor.extractSnippets(
                new StringReader(fullInputString),
                new SnippetExtractorCallback()
                {
                    int currentIndex = 0;

                    public void entireSnippetRegionMatch(
                        String matchedString, int streamStart, int streamEnd
                    )
                    {
                        // find 'out-of-token' space
                        int is = streamStart;
                        int ie = streamEnd;

                        is = adjustLeft(fullInputString, streamStart);
                        ie = adjustRight(fullInputString, streamEnd);

                        tokenizedStream.append(fullInputString.substring(currentIndex, is));
                        tokenizedStream.append(
                            "<span style=\"background-color: yellow;\"><font color=red>[</font>"
                        );
                        tokenizedStream.append(fullInputString.substring(is, ie));
                        tokenizedStream.append("<font color=red>]</font></span>");
                        currentIndex = ie;
                    }


                    public void snippetRecognized(SimpleSnippet s)
                    {
                        if (s == null)
                        {
                            tokenizedStream.append(fullInputString.substring(currentIndex));
                        }
                        else
                        {
                            tokenizedStream.append(
                                "<br><span style=\"background-color: red;\"><font color=white>[snippet recognized]</font></span><br>"
                            );
                            tokenizedStream.append(
                                "<span style=\"background-color: lightgrey;\"><font color=black><pre>\n"
                            );
                            tokenizedStream.append(
                                "Title: &gt;" + HtmlHelper.removeHtmlTags(s.getTitle()) + "&lt;\n"
                            );
                            tokenizedStream.append("URL  : &gt;" + s.getDocumentURL() + "&lt;\n");
                            tokenizedStream.append(
                                "Desc : &gt;" + HtmlHelper.removeHtmlTags(s.getSummary())
                                + "&lt;\n"
                            );
                            tokenizedStream.append("\n</pre></span>");
                        }
                    }


                    public boolean acceptSnippetWithEmptySummary()
                    {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NS]</font></span>"
                        );

                        return true;
                    }


                    public void snippetHasNoTitle()
                    {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NT]</font></span>"
                        );
                    }


                    public void snippetHasNoURL()
                    {
                        tokenizedStream.append(
                            "<span style=\"background-color: red;\"><font color=white>[NURL]</font></span>"
                        );
                    }


                    int adjustLeft(String s, int index)
                    {
                        int outoftoken = s.substring(0, index + 1).lastIndexOf("><", index);

                        if (outoftoken != -1)
                        {
                            if (fullInputString.charAt(outoftoken) == '<')
                            {
                                index = outoftoken - 1;
                            }
                        }

                        return index;
                    }


                    int adjustRight(String s, int index)
                    {
                        int outoftoken = s.indexOf("><", index);

                        if (outoftoken != -1)
                        {
                            if (fullInputString.charAt(outoftoken) == '>')
                            {
                                index = outoftoken + 1;
                            }
                        }

                        return index;
                    }
                }
            );
        }
        catch (Throwable e)
        {
            tokenizedStream.setLength(0);
            tokenizedStream.append(
                "<html><body>An exception occurred.<br><b>" + e.toString() + "</b><br><pre>"
                + ExceptionHelper.getStackTrace(e) + "</pre></body></html>"
            );
        }

        // remove scripts.
        String output = tokenizedStream.toString();
        String [] removeTokens = { "<script.*?</script>", "onLoad=[^> \t]*", "onClick=[^> \t]*" };

        for (int i = 0; i < removeTokens.length; i++)
        {
            RE removeMatches = new RE(removeTokens[i], RE.REG_ICASE | RE.REG_DOT_NEWLINE);
            output = removeMatches.substituteAll(output, "");
        }

        return output;
    }


    /**
     * Retrieves the source of a query.
     */
    public String getQuerySource(String query, int snippetsNeeded)
        throws Exception
    {
        StringBuffer stream = new StringBuffer();
        byte [] fullInput;

        try
        {
            String encoding;

            if (
                (encoding = JDOMHelper.getStringFromJDOM(
                            "/service/request#encoding", config, false
                        )) == null
            )
            {
                encoding = "iso8859-1";
            }

            InputStream is = reader.getQueryResults(
                    query, 80, encoding, JDOMHelper.getElement(
                        "/service/response/pageinfo", config
                    )
                );

            fullInput = FileHelper.readFullyAndCloseInput(is);

            stream.append(
                new String(
                    fullInput,
                    JDOMHelper.getStringFromJDOM("/service/response#encoding", config, true)
                )
            );
        }
        catch (Throwable e)
        {
            stream.setLength(0);
            stream.append(
                "<html><body>An exception occurred.<br><b>" + e.toString() + "</b><br><pre>"
                + ExceptionHelper.getStackTrace(e) + "</pre></body></html>"
            );
        }

        return stream.toString();
    }
}
