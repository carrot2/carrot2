

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


import com.dawidweiss.carrot.util.common.StringUtils;
import com.mwroblewski.carrot.lexical.Phrase;
import org.apache.log4j.Logger;
import java.util.*;


/**
 * @author Micha� Wr�blewski
 */
public class PhrasesExtractor
{
    public static final String SENTENCE_MARKER = ".";
    public static final String END_OF_TITLE_MARKER = "..";
    protected TermsFilterData data;
    protected Vector [] documents;
    protected Vector [] stemmedDocuments;
    protected Vector terms;
    protected int documentsCount;
    private final Logger log = Logger.getLogger(this.getClass());

    public PhrasesExtractor(
        Vector [] documents, Vector [] stemmedDocuments, Vector terms, TermsFilterData data
    )
    {
        this.documents = documents;
        this.stemmedDocuments = stemmedDocuments;
        this.data = data;
        documentsCount = documents.length;
    }

    protected float calculateAverageFrequency(HashMap frequenciesMap)
    {
        int frequenciesSum = 0;

        Iterator frequenciesIterator = frequenciesMap.values().iterator();

        while (frequenciesIterator.hasNext())
        {
            Integer frequency = (Integer) frequenciesIterator.next();
            frequenciesSum += frequency.intValue();
        }

        return (float) frequenciesSum / frequenciesMap.size();
    }


    protected float calculateFrequencyDeviation(HashMap frequenciesMap, float averageFrequency)
    {
        float differencesSum = 0;

        Iterator frequenciesIterator = frequenciesMap.values().iterator();

        while (frequenciesIterator.hasNext())
        {
            Integer frequency = (Integer) frequenciesIterator.next();
            differencesSum += Math.abs(averageFrequency - frequency.intValue());
        }

        return differencesSum / frequenciesMap.size();
    }


    protected Vector [] getUniquePhrases(int length, HashMap frequenciesMap, HashMap termsInTitles)
    {
        Vector [] phrasesInDocuments = new Vector[documentsCount];

        for (int i = 0; i < documentsCount; i++)
        {
            phrasesInDocuments[i] = new Vector();

            Vector document = documents[i];
            Vector stemmedDocument = stemmedDocuments[i];
            boolean processingTitle = true;
            int k;
            int l;

            for (int j = 0; j < (stemmedDocument.size() - length); j++)
            {
                String [] stems = new String[length];
                StringBuffer form = new StringBuffer();

                for (k = l = 0; k < length; l++)
                {
                    String stem = (String) stemmedDocument.elementAt(j + l);

                    if (l > 0)
                    {
                        form.append(" ");
                    }

                    if (stem == null)
                    {
                        // stop-word
                        if (k > 0)
                        {
                            form.append((String) document.elementAt(j + l));

                            continue;
                        }
                        else
                        {
                            break;
                        }
                    }

                    form.append(StringUtils.capitalize((String) document.elementAt(j + l)));

                    if (stem.equals(END_OF_TITLE_MARKER))
                    {
                        processingTitle = false;

                        break;
                    }
                    else if (stem.equals(SENTENCE_MARKER))
                    {
                        break;
                    }
                    else
                    {
                        stems[k++] = stem;
                    }
                }

                if (k == length)
                {
                    // found a possible phrase !!!
                    Phrase phrase = new Phrase(stems);

                    if (phrase.isSubphraseOrEquals(data.getStemmedQuery()))
                    {
                        // this phrase is equal to query or is it's subphrase
                        continue;
                    }

                    data.addForm(phrase.toString(), form.toString());
                    phrasesInDocuments[i].add(phrase);

                    Integer phraseFrequency = (Integer) frequenciesMap.get(phrase);

                    if (phraseFrequency == null)
                    {
                        // first occurrence of this phrase
                        frequenciesMap.put(phrase, new Integer(1));

                        boolean [] isInTitles = new boolean[documentsCount];

                        if (processingTitle)
                        {
                            isInTitles[i] = true;
                        }

                        termsInTitles.put(phrase, isInTitles);
                    }
                    else
                    {
                        phraseFrequency = new Integer(phraseFrequency.intValue() + 1);
                        frequenciesMap.put(phrase, phraseFrequency);

                        if (processingTitle)
                        {
                            boolean [] isInTitles = (boolean []) termsInTitles.get(phrase);
                            isInTitles[i] = true;
                        }
                    }
                }
            }
        }

        return phrasesInDocuments;
    }


    protected Vector [] getSpecifiedLengthPhrases(
        int length, float minStrength, HashMap termsInTitles
    )
    {
        HashMap frequenciesMap = new HashMap();
        Vector [] phrasesInDocuments = getUniquePhrases(length, frequenciesMap, termsInTitles);

        float averageFrequency = calculateAverageFrequency(frequenciesMap);
        float frequencyDeviation = calculateFrequencyDeviation(frequenciesMap, averageFrequency);
        float minFrequency = (frequencyDeviation * minStrength) + averageFrequency;

        // choosing phrases whose frequency exceeds minFrequency
        int count = frequenciesMap.size();

        for (int i = 0; i < documentsCount; i++)
        {
            Vector phrases = phrasesInDocuments[i];
            Vector newPhrases = new Vector();

            for (int j = 0; j < phrases.size(); j++)
            {
                Phrase phrase = (Phrase) phrases.elementAt(j);
                Integer f = (Integer) frequenciesMap.get(phrase);

                if (f != null)
                {
                    int frequency = f.intValue();

                    if (frequency > minFrequency)
                    {
                        newPhrases.add(phrase);
                    }
                    else
                    {
                        frequenciesMap.remove(phrase);
                    }
                }
            }

            phrasesInDocuments[i] = newPhrases;
        }

        log.debug(
            "length: " + length + " -> " + count + " distinct -> " + frequenciesMap.size()
            + " extracted"
        );
        log.debug(
            "avg: " + averageFrequency + " dev: " + frequencyDeviation + " min: " + minFrequency
        );

        return phrasesInDocuments;
    }


    public HashMap getPhrases(int maxLength, float minStrength, HashMap termsInTitles)
    {
        HashMap pfs = new HashMap();

        if (minStrength < 0.0f)
        {
            throw new IllegalArgumentException("minStrength: " + minStrength + " < 0.0");
        }

        if (maxLength < 2)
        {
            return pfs;
        }

        Vector [] phrasesInDocuments = new Vector[documentsCount];

        for (int i = 0; i < documentsCount; i++)
        {
            phrasesInDocuments[i] = new Vector();
        }

        for (int length = 2; length <= maxLength; length++)
        {
            Vector [] specifiedLengthPhrasesInDocuments = getSpecifiedLengthPhrases(
                    length, minStrength, termsInTitles
                );

            for (int i = 0; i < documentsCount; i++)
            {
                phrasesInDocuments[i].addAll(specifiedLengthPhrasesInDocuments[i]);
            }
        }

        // conversion of results to return format
        for (int i = 0; i < documentsCount; i++)
        {
            Vector phrases = phrasesInDocuments[i];

            for (int j = 0; j < phrases.size(); j++)
            {
                Phrase phrase = (Phrase) phrases.elementAt(j);

                int [] frequencies = (int []) pfs.get(phrase);

                if (frequencies == null)
                {
                    frequencies = new int[documentsCount];
                    frequencies[i] = 1;
                    pfs.put(phrase, frequencies);
                }
                else
                {
                    frequencies[i]++;
                }
            }
        }

        return pfs;
    }
}
