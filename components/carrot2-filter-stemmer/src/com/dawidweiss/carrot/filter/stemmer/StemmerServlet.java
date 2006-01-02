
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.filter.stemmer;


import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.core.local.linguistic.LanguageGuesser;
import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.Stemmer;
import com.dawidweiss.carrot.core.local.linguistic.tokens.StemmedToken;
import com.dawidweiss.carrot.core.local.linguistic.tokens.Token;
import com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken;
import com.dawidweiss.carrot.filter.langguesser.LanguageGuesserFactory;
import com.dawidweiss.carrot.util.tokenizer.languages.AllKnownLanguages;


/**
 * This a stemming servlet for Carrot2 search results clustering application.
 */
public class StemmerServlet
    extends com.dawidweiss.carrot.filter.FilterRequestProcessor
{
    private static Logger log = Logger.getLogger(StemmerServlet.class);

    private Map languages = new HashMap();

    
    /**
     * Creates a new stemmer/ language identifier servlet.
     *
     */
    public StemmerServlet()
    {
        String [] languageCodes = AllKnownLanguages.getLanguageCodes();
        for (int i = 0; i < languageCodes.length; i++)
        {
            this.languages.put(languageCodes[i], AllKnownLanguages
                .getLanguageForIsoCode(languageCodes[i]));
        }
    }

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
        final Element root = parseXmlStream(carrotData, "UTF-8");

        // Create a hashmap with stemmed terms.
        HashSet terms = new HashSet();

        String languages = (String) params.get("languages");
        if (request.getParameter("languages") != null) {
            languages = request.getParameter("languages");
        }

        LanguageGuesser guesser = null;
        String defaultLanguage = null;
        
        if (languages != null) {
            // restrict the detected languages to a subset...
            List restricted = splitLanguages( languages );
            if (restricted.isEmpty()) {
                log.warn("Language restriction is empty (no supported languages?).");
            } else {
                if (restricted.size() == 1)
                    defaultLanguage = (String) restricted.get(0);
                else
                	guesser = LanguageGuesserFactory.getLanguageGuesser(restricted);
            }

        } else {
            List known = Arrays.asList(this.languages.keySet().toArray());
            guesser = LanguageGuesserFactory.getLanguageGuesser(known);
        }

        // for every //document element, process it's contents - title and snippet subelements
        ArrayList stemmedTerms = new ArrayList(100);

        final Element newRoot = new DocumentFactory().createElement(root.getName());
        final List documents = root.elements();

        for (Iterator i = documents.iterator(); i.hasNext();)
        {
            final Element current = (Element) i.next();
            current.detach();

            if ("document".equals(current.getName()))
            {
                // check if it's only a reference perhaps
                if (current.attribute("refid") == null)
                {
                    String concat = null;
                    
                    // not a reference.
                    final Element title = current.element("title");
                    final Element snippet = current.element("snippet");

                    if (title != null) {
                        concat = title.getText();
                    }
                    if (snippet != null) {
                        concat = (concat == null ? "" : concat + " ") + snippet.getText();
                    }

                    char [] chars = concat.toCharArray();
                    String language = defaultLanguage;
                    if (guesser != null)
                    	language = guesser.guessLanguage(chars, 0, chars.length);

                    Language lang = null;

                    if (language != null) {
                        current.addAttribute("lang", language);
                        lang = (Language) this.languages.get(language);
                        
                        if (title != null)
                        {
                            process(title, terms, lang, stemmedTerms);
                        }

                        if (snippet != null)
                        {
                            process(snippet, terms, lang, stemmedTerms);
                        }                        
                    }

                    for (int j = 0; j < stemmedTerms.size(); j++)
                    {
                        newRoot.add((Element) stemmedTerms.get(j));
                    }

                    stemmedTerms.clear();
                }
            }

            newRoot.add(current);
        }

        // save the output.
        serializeXmlStream(newRoot, response.getOutputStream(), "UTF-8");
    }

    private List splitLanguages(String languages) {
        StringTokenizer tokenizer = new StringTokenizer(languages, ",");
        List restriction = new ArrayList();
        while (tokenizer.hasMoreTokens()) {
            String lang = tokenizer.nextToken().trim();
            if (this.languages.containsKey(lang))
                restriction.add(lang);
        }
        return restriction;
    }
    

    /**
     * This method processes the textual contents of a given node, removing all non-letter
     * characters, but leaving sentence boundaries.
     */
    private final void process(
        Element node, HashSet terms, Language lang, List newTerms)
    {
        final DocumentFactory factory = new DocumentFactory();

        Token [] tokens = new Token [ 30 ];
        StringBuffer buffer = new StringBuffer(20);

        Stemmer s = null;        
        try {
            s = lang.borrowStemmer();
            LanguageTokenizer tokenizer = null;
            try {
                tokenizer = lang.borrowTokenizer();
                
                tokenizer.restartTokenizationOn(new StringReader( node.getText()));

                while (true) {
                	int num = tokenizer.getNextTokens(tokens, 0);
                    
                    if (num > 0 ) {
                        for (int i=0;i<num;i++) {
                            try {
                                StemmedToken token = (StemmedToken) tokens[i];
                                buffer.setLength(0);
                                token.appendTo(buffer);
                                String image = buffer.toString();
                                if (token instanceof TypedToken) {
                                    if (((((TypedToken) token).getType() & TypedToken.MASK_TOKEN_TYPE) & TypedToken.TOKEN_TYPE_TERM) == 0) {
                                        continue;
                                    }
                                }
                                
                                if (terms.contains(image))
                                    continue;

                                terms.add(image);
                                String stem = token.getStem();
                                boolean isStopWord = false;
                                
                                // Is the term on stop-words list?
                                if (token instanceof TypedToken)
                                {
                                    if ( (((TypedToken) token).getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0) {
                                        isStopWord = true;
                                    }
                                }
                                
                                if (stem == null && isStopWord) {
                                    stem = image;
                                }

                                if (stem != null && (isStopWord || !image.equals(stem))) {
                                    Element stemmedForm = factory.createElement("l");
                                    newTerms.add(stemmedForm);

                                    stemmedForm.addAttribute("t", image);
                                    stemmedForm.addAttribute("s", stem);
                                    stemmedForm.addAttribute("lang", lang.getIsoCode());

                                    if (isStopWord) {
                                        stemmedForm.addAttribute("sw", "");
                                    }
                                }
                            } catch (ClassCastException e) {
                                // ignore tokenizers that don't produce stemmed tokens.
                                log.warn("Tokenizer does not produce stemmed tokens: " + lang.getIsoCode());
                                this.languages.remove(lang.getIsoCode());
                            }
                        }
                    } else break;
                }
            } finally {
                if (tokenizer != null) lang.returnTokenizer(tokenizer);
            }
        } finally {
            if (s != null) lang.returnStemmer(s);
        }
    }
}
