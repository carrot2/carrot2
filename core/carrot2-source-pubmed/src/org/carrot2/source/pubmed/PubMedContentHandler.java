
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.pubmed;

import java.util.Arrays;
import java.util.Set;

import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * A SAX content handler that collects the contents of PubMed abstracts.
 */
class PubMedContentHandler extends PathTrackingHandler
{
    /** Collects PubMed results */
    private SearchEngineResponse response;

    public PubMedContentHandler()
    {
        super.addTrigger(Arrays.asList(
            "/PubmedArticleSet/PubmedArticle", 
            "/PubmedArticleSet/PubmedBookArticle"), new Trigger()
        {
            String pmid;
            String title;
            StringBuilder body = new StringBuilder();
            
            {
                addTrigger(Arrays.asList(
                    "/PubmedArticleSet/PubmedArticle/MedlineCitation/PMID",
                    "/PubmedArticleSet/PubmedBookArticle/BookDocument/PMID"), new Trigger() {
                    @Override
                    public void afterElement(String localName, String path, String text)
                    {
                        assert pmid == null;
                        pmid = text;
                    }
                });

                addTrigger(Arrays.asList(
                    "/PubmedArticleSet/PubmedArticle/MedlineCitation/Article/ArticleTitle",
                    "/PubmedArticleSet/PubmedBookArticle/BookDocument/Book/ArticleTitle"), new Trigger() {
                    @Override
                    public void afterElement(String localName, String path, String text)
                    {
                        assert title == null;
                        title = text;
                    }
                });

                addTrigger(Arrays.asList(
                    "/PubmedArticleSet/PubmedArticle/MedlineCitation/Article/Abstract/AbstractText",
                    "/PubmedArticleSet/PubmedBookArticle/BookDocument/Book/Abstract/AbstractText"), new Trigger() {
                    Set<String> skipLabels = Sets.newHashSet(
                        "CONCLUSIONS", 
                        "METHODS", 
                        "RESULTS",
                        "DIAGNOSIS/TESTING",
                        "MANAGEMENT",
                        "GENETIC COUNSELING");
                    String label;

                    @Override
                    public void onElement(String localName, String path, Attributes attrs)
                    {
                        label = attrs.getValue("", "NlmCategory");
                    }

                    @Override
                    public void afterElement(String localName, String path, String text)
                    {
                        if (label == null || !skipLabels.contains(label)) {
                            if (body.length() > 0) {
                                body.append(" ... ");
                            }
                            body.append(text);
                        }
                    }
                });
            }

            @Override
            public void onElement(String localName, String path, Attributes attrs)
            {
                pmid = title = null;
                body.setLength(0);
            }
            
            @Override
            public void afterElement(String localName, String path, String text)
            {
                if (pmid != null) {
                    response.results.add(new Document(title, body.toString(),
                        "http://www.ncbi.nlm.nih.gov/pubmed/" + pmid, null, pmid));
                } else {
                    LoggerFactory.getLogger(PubMedContentHandler.class).warn("No PMID on a <PubmedArticle>?");
                }
            }
        });
    }
    
    @Override
    public void startDocument() throws SAXException
    {
        this.response = new SearchEngineResponse();
    }

    public SearchEngineResponse getResponse()
    {
        return response;
    }
}
