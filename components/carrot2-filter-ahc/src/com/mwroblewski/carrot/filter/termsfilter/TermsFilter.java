

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.termsfilter;


import com.dawidweiss.carrot.filter.FilterRequestProcessor;
import com.dawidweiss.carrot.tokenizer.Tokenizer;
import com.dawidweiss.carrot.util.common.StringUtils;
import com.mwroblewski.carrot.filter.termsfilter.weighing.TermsWeighing;
import com.mwroblewski.carrot.lexical.Phrase;
import com.mwroblewski.carrot.lexical.Term;
import com.mwroblewski.carrot.utils.LogUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Michał Wróblewski
 */
public class TermsFilter
    extends FilterRequestProcessor
{
    protected File tmpFile;
    protected Tokenizer tokenizer = Tokenizer.getTokenizer();
    private final Logger log = Logger.getLogger(this.getClass());

    public TermsFilter()
    {
        this.tmpFile = null;
    }


    public TermsFilter(File tmpFile)
    {
        this.tmpFile = tmpFile;
    }

    public void processFilterRequest(
        InputStream carrotData, HttpServletRequest request, HttpServletResponse response, Map params
    )
        throws Exception
    {
        // obtaining values of parameters
        log.debug("Obtained params:\n" + params);

        TermsFilterParams filterParams = new TermsFilterParams(params);
        TermsWeighing termsWeighing = filterParams.getTermsWeighing();
        int maxPhrasesLength = filterParams.getMaxPhrasesLength();
        float minPhrasesStrength = filterParams.getMinPhrasesStrength();
        float strongTermsWeight = filterParams.getStrongTermsWeight();
        boolean removeQuery = filterParams.getRemoveQuery();
        boolean removeSingleTerms = filterParams.getRemoveSingleTerms();

        // phase 1 -
        // creating an array snippets and (possibly) hash map of stems from
        // XML input stream
        Date before = new Date();

        Element root = parseXmlStream(carrotData, "UTF-8");
        TermsFilterData data = new TermsFilterData(root);

        String [] snippets = data.getSnippets();
        String [] titles = data.getTitles();

        log.info(
            "read input & extracted snippets & titles in " + LogUtils.timeTillNow(before) + " ms."
        );

        // phase 2 -
        // extracting terms & calculating terms & document frequencies
        before = new Date();

        Vector lexicalElements = new Vector();
        HashMap tmpTFs = new HashMap(snippets.length * 5);
        HashMap termsInTitles = new HashMap(snippets.length);
        Vector [] documents = new Vector[snippets.length];
        Vector [] stemmedDocuments = new Vector[snippets.length];
        Vector document;
        Vector stemmedDocument;
        String word;
        String stem;
        int [] type = new int[1];
        boolean processingTitles;

        synchronized (tokenizer)
        {
            for (int i = 0; i < snippets.length; i++)
            {
                document = new Vector();
                stemmedDocument = new Vector();
                documents[i] = document;
                stemmedDocuments[i] = stemmedDocument;
                tokenizer.restartTokenizerOn(titles[i]);
                processingTitles = true;

                for (;;)
                {
                    word = tokenizer.getNextToken(type);

                    if (word == null)
                    {
                        if (processingTitles)
                        {
                            tokenizer.restartTokenizerOn(snippets[i]);
                            processingTitles = false;

                            // if there was no sentence marker at the end of
                            // the title
                            document.add(PhrasesExtractor.END_OF_TITLE_MARKER);
                            stemmedDocument.add(PhrasesExtractor.END_OF_TITLE_MARKER);

                            continue;
                        }
                        else
                        {
                            // if there was no sentence marker at the end of
                            // the snippet
                            document.add(PhrasesExtractor.SENTENCE_MARKER);
                            stemmedDocument.add(PhrasesExtractor.SENTENCE_MARKER);

                            break;
                        }
                    }

                    if (
                        word.equals("quot") || word.equals("gt") || word.equals("lt")
                            || word.equals("amp")
                    )
                    {
                        continue;
                    }

                    switch (type[0])
                    {
                        case Tokenizer.TYPE_SENTENCEMARKER:
                        {
                            // end of the sentence
                            document.add(PhrasesExtractor.SENTENCE_MARKER);
                            stemmedDocument.add(PhrasesExtractor.SENTENCE_MARKER);

                            break;
                        }

                        default:
                            log.warn("unrecognized token type: " + type[0] + " - treated as term");

                        case Tokenizer.TYPE_EMAIL:
                        case Tokenizer.TYPE_URL:
                        case Tokenizer.TYPE_TERM:
                        case Tokenizer.TYPE_PERSON:
                        {
                            document.add(word);
                            stem = data.getStem(word);

                            if ((stem == null) || data.isStopWord(word))
                            {
                                stemmedDocument.add(null);

                                continue;
                            }

                            stemmedDocument.add(stem);
                            data.addForm(stem, StringUtils.capitalize(word));

                            if (
                                removeQuery //data.stemmedQueryVector.contains(stem))
                                    && stem.equals(data.getStemmedQuery())
                            )
                            {
                                continue;
                            }

                            // found a valid term
                            Term term = new Term(stem);
                            int termIndex = lexicalElements.indexOf(term);

                            if (termIndex == -1)
                            {
                                lexicalElements.addElement(term);

                                int [] tf = new int[snippets.length];
                                tf[i] = 1;
                                tmpTFs.put(term, tf);

                                boolean [] isInTitles = new boolean[snippets.length];

                                if (processingTitles)
                                {
                                    isInTitles[i] = true;
                                }

                                termsInTitles.put(term, isInTitles);
                            }
                            else
                            {
                                term = (Term) lexicalElements.elementAt(termIndex);

                                int [] tf = (int []) tmpTFs.get(term);
                                tf[i]++;

                                if (processingTitles)
                                {
                                    boolean [] isInTitles = (boolean []) termsInTitles.get(term);
                                    isInTitles[i] = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // adding phrases to term vector & tfs map
        PhrasesExtractor phrasesExtractor = new PhrasesExtractor(
                documents, stemmedDocuments, lexicalElements, data
            );
        HashMap pfs = phrasesExtractor.getPhrases(
                maxPhrasesLength, minPhrasesStrength, termsInTitles
            );
        Iterator pfsIterator = pfs.entrySet().iterator();

        while (pfsIterator.hasNext())
        {
            Map.Entry entry = (Map.Entry) pfsIterator.next();

            Phrase phrase = (Phrase) entry.getKey();
            int [] frequencies = (int []) entry.getValue();

            lexicalElements.add(phrase);
            tmpTFs.put(phrase, frequencies);
        }

        int [][] tfs = new int[lexicalElements.size()][];

        for (int i = 0; i < lexicalElements.size(); i++)
        {
            tfs[i] = (int []) tmpTFs.get(lexicalElements.elementAt(i));
        }

        // calculating document frequencies
        int [] dfs = new int[tfs.length];

        for (int i = 0; i < dfs.length; i++)
        {
            for (int j = 0; j < snippets.length; j++)
            {
                if (tfs[i][j] > 0)
                {
                    dfs[i]++;
                }
            }
        }

        // removing terms / phrase that appear in only one document
        if (removeSingleTerms)
        {
            // marking single terms & calcuating their number
            int multipleTermsNumber = 0;

            for (int i = 0; i < lexicalElements.size(); i++)
            {
                if (dfs[i] > 1)
                {
                    multipleTermsNumber++;
                }
            }

            int [] multipleDFs = new int[multipleTermsNumber];
            int [][] multipleTFs = new int[multipleTermsNumber][];

            int j = 0;
            int size = lexicalElements.size();

            for (int i = 0; i < size; i++)
            {
                if (dfs[i] > 1)
                {
                    multipleDFs[j] = dfs[i];
                    multipleTFs[j] = tfs[i];
                    j++;
                }
                else
                {
                    lexicalElements.removeElementAt(j);
                }
            }

            dfs = multipleDFs;
            tfs = multipleTFs;
        }

        log.info(
            "extracted terms & calculated tfs & dfs in " + LogUtils.timeTillNow(before) + " ms."
        );
        log.debug("terms: " + lexicalElements);
        log.debug("tfs: ");
        log.debug(LogUtils.arrayToString(tfs));
        log.debug("dfs: ");
        log.debug(LogUtils.arrayToString(dfs));

        // phase 3 -
        // calculating terms weights
        before = new Date();

        float [][] termsWeights = termsWeighing.weighTerms(tfs, dfs);

        // increasing weights of terms that appear in titles
        for (int i = 0; i < lexicalElements.size(); i++)
        {
            boolean [] isInTitles = (boolean []) termsInTitles.get(lexicalElements.elementAt(i));

            for (int j = 0; j < snippets.length; j++)
            {
                if (isInTitles[j])
                {
                    termsWeights[i][j] = termsWeights[i][j] * strongTermsWeight;
                }
            }
        }

        log.info("calculated terms weights in " + LogUtils.timeTillNow(before) + " ms.");
        log.debug("terms weights : ");
        log.debug(LogUtils.arrayToString(termsWeights));

        // phase 4 -
        // saving the data (terms, their weights and occurrences in documents)
        // to the output XML stream
        before = new Date();

        data.removeGroups();
        data.saveLexicalElements(lexicalElements, termsWeights);
        data.removeLexicalInformation();

        if (tmpFile != null)
        {
            FileOutputStream fos = new FileOutputStream(tmpFile);
            serializeXmlStream(root, fos, "UTF-8");
            fos.close();
        }
        else
        {
            serializeXmlStream(root, response.getOutputStream(), "UTF-8");
        }

        log.info("saved data in " + LogUtils.timeTillNow(before) + " ms.");
    }
}
