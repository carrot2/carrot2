

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


package com.dawidweiss.carrot.filter.stemming;


import org.apache.log4j.Logger;
import org.jdom.Element;
import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This a stemming servlet for Carrot2 search results clustering application.
 */
public class StemmerServlet
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    private static Logger log = Logger.getLogger(StemmerServlet.class);
    private Map stemmers = new HashMap();
    private Map stopwordFiles = new HashMap();

    public StemmerServlet()
    {
        stopwordFiles.put("", Collections.EMPTY_SET);
    }

    /**
     * If <code>true</code>, terms from the input will be converted to lowercase before stemming is
     * applied. This decreases the number of term variations in the output, but should not affect
     * quality at all since most stemmers perform lowercase conversion anyway.
     */
    public static final boolean LOWERCASE_EVERYTHING = true;

    /**
     * Filters Carrot2 XML data as specified in class description.
     *
     * @param carrotData A valid InputStream to search results data as specified in the Manual.
     * @param request Http request which caused this processing (not used in this filter)
     * @param response Http response for this request
     * @param params A map of parameters sent before data stream (unused in this filter)
     */
    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        // parse input data (must be UTF-8 encoded).
        Element root = parseXmlStream(carrotData, "UTF-8");

        // Create a hashmap with stemmed terms.
        HashSet terms = new HashSet();

        // Choose stemmer and stop words list
        if (request.getQueryString() == null)
        {
            log.warn("No input parameters specified.");
            response.getOutputStream().write(
                "Arguments are required for this filter. Consult the docs.".getBytes()
            );

            return;
        }

        Map urlParams = com.dawidweiss.carrot.util.CommonComponentInitializationServlet
            .parseQueryString(request.getQueryString(), "UTF-8");

        Object stemmerClass = urlParams.get("stemmer");
        Object stopwordsFile = urlParams.get("stopwords");

        if ((stemmerClass == null) || !(stemmerClass instanceof String))
        {
            log.warn("Stemmer class not specified or more than one value: " + stemmerClass);
            response.getOutputStream().write("Invalid stemmer class name (or null)".getBytes());

            return;
        }

        // Stemmer used to process input.
        DirectStemmer stemmer = null;

        if ((stemmer = (DirectStemmer) stemmers.get(stemmerClass)) == null)
        {
            log.debug("Instantiating stemmer: " + stemmerClass);

            try
            {
                Class cl = this.getClass().getClassLoader().loadClass((String) stemmerClass);
                stemmer = (DirectStemmer) cl.newInstance();
                stemmers.put(stemmerClass, stemmer);
            }
            catch (ClassNotFoundException e)
            {
                log.error("Stemmer class: " + stemmerClass + " not found.");
                response.getOutputStream().write(
                    ("Stemmer class: " + stemmerClass + " not found.").getBytes()
                );

                return;
            }
            catch (Exception e)
            {
                log.error("Stemmer class could not be loaded: " + stemmerClass, e);
                response.getOutputStream().write(
                    ("Stemmer class could not be loaded: " + stemmerClass).getBytes()
                );

                return;
            }
        }

        // Stop words list.
        Set stopwords;

        if (stopwordsFile == null)
        {
            stopwordsFile = "";
        }

        if (stopwordsFile instanceof List)
        {
            log.error("More than one stop words list specified: " + stopwordsFile);
            response.getOutputStream().write(
                "Just one stop words list can be specified.".getBytes()
            );

            return;
        }

        if ((stopwords = (Set) stopwordFiles.get(stopwordsFile)) == null)
        {
            // attempt to load the stop words files.
            addStopWordsSet((String) stopwordsFile);
            stopwords = (Set) stopwordFiles.get(stopwordsFile);

            if (stopwords == null)
            {
                log.error("Stop words file could not be initialized: " + stopwordsFile);
                response.getOutputStream().write(
                    ("Stop words file could not be initialized: " + stopwordsFile).getBytes()
                );

                return;
            }
        }

        // for every //document element, process it's contents - title and snippet subelements
        ArrayList stemmedTerms = new ArrayList(100);

        List documents = root.getChildren();
        ArrayList children = new ArrayList(documents);
        root.getChildren().clear();

        for (int i = 0; i < children.size(); i++)
        {
            Element current = (Element) children.get(i);

            if ("document".equals(current.getName()))
            {
                // check if it's only a reference perhaps
                if (current.getAttribute("refid") == null)
                {
                    // not a reference.
                    Element title = current.getChild("title");

                    if (title != null)
                    {
                        process(title, terms, stemmer, stemmedTerms, stopwords);
                    }

                    Element snippet = current.getChild("snippet");

                    if (snippet != null)
                    {
                        process(snippet, terms, stemmer, stemmedTerms, stopwords);
                    }

                    for (int j = 0; j < stemmedTerms.size(); j++)
                    {
                        root.addContent((Element) stemmedTerms.get(j));
                    }

                    stemmedTerms.clear();
                }
            }

            root.addContent(current);
        }

        // save the output.
        serializeXmlStream(root, response.getOutputStream(), "UTF-8");
    }


    // ------------------------------------------------------- protected section
    private void addStopWordsSet(String file)
    {
        File stopwords = new File(
                getServletConfig().getServletContext().getRealPath("stopwords/" + file)
            );

        if (stopwords.canRead() == false)
        {
            log.error("Stop words file unreadable: " + stopwords.getAbsolutePath());
        }
        else
        {
            Reader r = null;
            HashSet stopwordset = new HashSet();

            try
            {
                r = new InputStreamReader(new FileInputStream(stopwords), "UTF-8");

                StreamTokenizer st = new StreamTokenizer(r);

                int token;

                while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
                {
                    switch (token)
                    {
                        case StreamTokenizer.TT_WORD:
                            stopwordset.add(st.sval);

                            break;

                        default:
                            log.error("Unrecognized token: " + token);
                    }
                }

                log.debug(
                    "Adding file: " + stopwords.getAbsolutePath() + " as a stop words set named: "
                    + file
                );
                this.stopwordFiles.put(file, stopwordset);
            }
            catch (IOException e)
            {
                log.error("Cannot read stop words file: " + stopwords.getAbsoluteFile(), e);
            }
            finally
            {
                if (r != null)
                {
                    try
                    {
                        r.close();
                    }
                    catch (IOException x)
                    {
                        log.error("Cannot close file."); /* not much we can do. */
                    }
                }
            }
        }
    }


    /**
     * This method processes the textual contents of a given node, removing all non-letter
     * characters, but leaving sentence boundaries.
     */
    private final void process(
        Element node, HashSet terms, DirectStemmer directStemmer, List newTerms, Set stopwords
    )
    {
        String nodeTextString;
        char [] nodeText;

        if (LOWERCASE_EVERYTHING)
        {
            nodeTextString = node.getText().toLowerCase();
        }
        else
        {
            nodeTextString = node.getText();
        }

        nodeText = nodeTextString.toCharArray();

        int i = 0;

        while (i < nodeText.length)
        {
            while ((i < nodeText.length) && !Character.isLetter(nodeText[i]))
            {
                i++;
            }

            int j = i;

            do
            {
                i++;
            }
            while ((i < nodeText.length) && Character.isLetter(nodeText[i]));

            if ((i - j) >= 2)
            {
                // we've got at least 2-letter term.
                String term = nodeTextString.substring(j, i);

                if (terms.contains(term) == false)
                {
                    String stem = directStemmer.getStem(nodeText, j, i);

                    if (stem == null)
                    {
                        continue;
                    }

                    Element stemmedForm = new Element("l");
                    newTerms.add(stemmedForm);

                    stemmedForm.setAttribute("t", term);
                    stemmedForm.setAttribute("s", stem);

                    // Is the term on stop-words list?
                    if (stopwords.contains(term))
                    {
                        stemmedForm.setAttribute("sw", "");
                    }

                    terms.add(term);
                }
            }
        }
    }
}
