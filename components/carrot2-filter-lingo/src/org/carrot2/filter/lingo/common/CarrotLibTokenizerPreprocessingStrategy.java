
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.common;

import org.carrot2.core.linguistic.Language;
import org.carrot2.core.linguistic.Stemmer;

import org.carrot2.filter.lingo.tokenizer.*;
import org.carrot2.util.StringUtils;

import org.apache.log4j.Logger;

import java.util.*;


/**
 * A preprocessing strategy utilizing an internal tokenizer, languages map and
 * stemmers from the new Carrot2 core.
 * 
 * @author Dawid Weiss
 * @author Stanisław Osiński
 */
public final class CarrotLibTokenizerPreprocessingStrategy
    implements PreprocessingStrategy {
    /**
     * Logger
     */
    protected static final Logger logger = Logger.getLogger(CarrotLibTokenizerPreprocessingStrategy.class);

    /**
     * Linguistic information
     */
    protected Map stemSets;

    protected Map inflectedSets;

    protected Map stopWordSets;

    protected Map nonStopWordSets;

    protected Map languages;

    protected Set strongWords;

    protected Set queryWords;

    protected Set lowCaseWords;

    protected Map caseCheck;

    protected Map inflectedFreqSets;

    public CarrotLibTokenizerPreprocessingStrategy() {
    }

    public Snippet[] preprocess(AbstractClusteringContext clusteringContext) {
        Tokenizer tokenizer = JFlexTokenizer.getTokenizer();

        Snippet[] snippets = clusteringContext.getSnippets();
        Snippet[] preprocessedSnippets = new Snippet[snippets.length];

        stopWordSets = ((MultilingualClusteringContext) clusteringContext).getStopWordSets();
        nonStopWordSets = ((MultilingualClusteringContext) clusteringContext).getNonStopWordSets();
        stemSets = ((MultilingualClusteringContext) clusteringContext).getStemSets();
        inflectedSets = ((MultilingualClusteringContext) clusteringContext).getInflectedSets();
        strongWords = ((MultilingualClusteringContext) clusteringContext).getStrongWords();
        queryWords = ((MultilingualClusteringContext) clusteringContext).getQueryWords();
        languages = ((MultilingualClusteringContext) clusteringContext).getLanguages();

        inflectedFreqSets = new HashMap();

        lowCaseWords = new HashSet();
        caseCheck = new HashMap();

        // Clean and guess language
        for (int i = 0; i < snippets.length; i++) {
            preprocessedSnippets[i] = preprocess(snippets[i], tokenizer);
        }

        // Change "unidentified" to the most common language
        HashMap languageFreq = new HashMap();
        String mostCommonLanguage = MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME;
        int maxLanguageFreq = 0;

        for (int i = 0; i < preprocessedSnippets.length; i++) {
            if (!languageFreq.containsKey(preprocessedSnippets[i].getLanguage())) {
                languageFreq.put(preprocessedSnippets[i].getLanguage(), new Integer(1));

                if ((maxLanguageFreq < 1) &&
                        !preprocessedSnippets[i].getLanguage().equals(MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME)) {
                    maxLanguageFreq = 1;
                    mostCommonLanguage = preprocessedSnippets[i].getLanguage();
                }
            } else {
                int freq = ((Integer) languageFreq.get(preprocessedSnippets[i].getLanguage())).intValue();

                languageFreq.put(preprocessedSnippets[i].getLanguage(),
                    new Integer(freq + 1));

                if ((maxLanguageFreq < (freq + 1)) &&
                        !preprocessedSnippets[i].getLanguage().equals(MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME)) {
                    maxLanguageFreq = freq + 1;
                    mostCommonLanguage = preprocessedSnippets[i].getLanguage();
                }
            }
        }

        for (int i = 0; i < snippets.length; i++) {
            if (preprocessedSnippets[i].getLanguage().equals(MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME)) {
                preprocessedSnippets[i].setLanguage(mostCommonLanguage);
            }

            if ( ((MultilingualClusteringContext)clusteringContext).DISABLE_STEMMING ) continue;
            preprocessedSnippets[i] = stemming(preprocessedSnippets[i]);
        }

        // Create inflectedSets
        Iterator languages = inflectedFreqSets.keySet().iterator();

        while (languages.hasNext()) {
            String language = (String) languages.next();
            HashMap inflectedFreq = (HashMap) inflectedFreqSets.get(language);

            HashMap inflected = new HashMap();
            inflectedSets.put(language, inflected);

            Iterator stems = inflectedFreq.keySet().iterator();

            while (stems.hasNext()) {
                String stem = (String) stems.next();

                HashMap inflectedForStem = (HashMap) inflectedFreq.get(stem);

                if (inflectedForStem != null) {
                    int maxFreq = 0;
                    String bestInflected = stem;
                    Iterator inflecteds = inflectedForStem.keySet().iterator();

                    while (inflecteds.hasNext()) {
                        String infl = (String) inflecteds.next();
                        Integer freq = (Integer) inflectedForStem.get(infl);

                        if (freq.intValue() > maxFreq) {
                            maxFreq = freq.intValue();
                            bestInflected = infl;
                        }
                    }

                    inflected.put(stem, bestInflected);
                }
            }
        }

        return preprocessedSnippets;
    }

    /**
     * Method clean.
     */
    protected Snippet preprocess(Snippet snippet, Tokenizer tokenizer) {
        String title = tokenizeAndClean(snippet.getTitle(), tokenizer);
        String body = tokenizeAndClean(snippet.getBody(), tokenizer);

        // check, maybe we already have the language?
        String language = snippet.getLanguage();

        if (language != MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME) {
            // language for this snippet has already been set.
        } else {
            String chunk;

            if (!"".equals(title)) {
                chunk = title;
            } else {
                chunk = null;
            }

            if (!"".equals(body)) {
                if (chunk == null) {
                    chunk = body;
                } else {
                    chunk += (". " + body);
                }
            }

            if (chunk == null) {
                chunk = "";
            }

            language = guessLanguage(chunk);
        }

        Snippet preprocessedSnippet = new Snippet(snippet.getSnippetId(),
                title, body, language);
        preprocessedSnippet.setLocation(snippet.getLocation());

        return preprocessedSnippet;
    }

    protected Snippet stemming(Snippet snippet) {
        Snippet stemmedSnippet = new Snippet(snippet.getSnippetId(),
                stemming(snippet.getTitle(), snippet.getLanguage(), true),
                stemming(snippet.getBody(), snippet.getLanguage(), false),
                snippet.getLanguage());
        stemmedSnippet.setLocation(snippet.getLocation());

        return stemmedSnippet;
    }

    private String stemming(String text, String langCode, boolean strong) {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        Language language;

        if (langCode != null) {
            language = (Language) languages.get(langCode);
        } else {
            language = null;
        }

        Stemmer stemmer = (((language == null) ? null : language.borrowStemmer()));

        try {
            HashMap stems = (HashMap) stemSets.get(langCode);
            HashSet stopWords = (HashSet) stopWordSets.get(langCode);
            HashSet nonStopWords = (HashSet) nonStopWordSets.get(langCode);

            if ((stems == null) || (stopWords == null) ||
                    (nonStopWords == null)) {
                // throw new RuntimeException("Internal error: " + langCode);
                // we allow unrecognized languages on input.
            }

            if (!inflectedFreqSets.containsKey(langCode)) {
                inflectedFreqSets.put(langCode, new HashMap());
            }

            HashMap inflectedFreq = (HashMap) inflectedFreqSets.get(langCode);

            while (stringTokenizer.hasMoreTokens()) {
                String token = stringTokenizer.nextToken();

                if (token.equals(".")) {
                    if (stringBuffer.length() > 0) {
                        stringBuffer.append(" .");
                    }

                    continue;
                }

                // Remove one-character-long terms          
                if ((token.length() < 2) &&
                        ((stopWords == null) ||
                        ((stopWords != null) &&
                        !stopWords.contains(token.toLowerCase())))) {
                    continue;
                }

                // Remove overly long terms
                if (token.length() > 25) {
                    continue;
                }

                // Case processing
                if (StringUtils.capitalizedRatio(token) > 0.5) {
                    if (lowCaseWords.contains(token.toLowerCase())) {
                        token = token.toLowerCase();
                    }
                } else {
                    token = token.toLowerCase();
                }

                // Stemming
                if ((stemmer != null) &&
                        !langCode.equalsIgnoreCase(
                            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME) &&
                        !stopWords.contains(token)) {
                    String stem;

                    if (!stems.containsKey(token)) {
                        stem = stemmer.getStem(token.toCharArray(), 0,
                                token.length());

                        if (stem != null) // ineffective !
                         {
                            stems.put(token, stem);
                        } else {
                            stems.put(token, token);
                        }
                    } else {
                        stem = (String) stems.get(token);
                    }

                    if (!inflectedFreq.containsKey(stem)) {
                        inflectedFreq.put(stem, new HashMap());
                    }

                    HashMap inflectedForStem = (HashMap) inflectedFreq.get(stem);

                    if (!inflectedForStem.containsKey(token)) {
                        inflectedForStem.put(token, new Integer(1));
                    } else {
                        Integer freq = (Integer) inflectedForStem.get(token);
                        inflectedForStem.put(token,
                            new Integer(freq.intValue() + 1));
                    }

                    token = (String) stems.get(token);
                }

                // Strong terms
                if (strong) {
                    strongWords.add(token);
                }

                // Non-stop words
                if (langCode.equalsIgnoreCase(
                            MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME) ||
                        !stopWords.contains(token)) {
                    nonStopWords.add(token);
                }

                if (stringBuffer.length() == 0) {
                    stringBuffer.append(token);
                } else {
                    stringBuffer.append(" ");
                    stringBuffer.append(token);
                }
            }
        } finally {
            if (stemmer != null) {
                language.returnStemmer(stemmer);
            }
        }

        return stringBuffer.toString();
    }

    private String guessLanguage(String text) {
        StringTokenizer stringTokenizer = new StringTokenizer(text);

        HashMap stopWordFrequencies = new HashMap();
        String language = MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME;
        int maxStopWordFrequency = 0;

        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();

            if (StringUtils.capitalizedRatio(token) > 0.5) {
                continue;
            }

            if (token.equals(".")) {
                continue;
            }

            Iterator keys = stopWordSets.keySet().iterator();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                HashSet stopWords = (HashSet) stopWordSets.get(key);

                if (stopWords.contains(token)) {
                    if (!stopWordFrequencies.containsKey(key)) {
                        stopWordFrequencies.put(key, new Integer(1));

                        if (1 > maxStopWordFrequency) {
                            maxStopWordFrequency = 1;
                            language = key;
                        }
                    } else {
                        int stopWordFrequency = ((Integer) stopWordFrequencies.get(key)).intValue();
                        stopWordFrequencies.put(key,
                            new Integer(stopWordFrequency + 1));

                        if ((stopWordFrequency + 1) > maxStopWordFrequency) {
                            maxStopWordFrequency = stopWordFrequency + 1;
                            language = key;
                        }
                    }
                }
            }
        }

        // Check for "draws"
        boolean draw = false;
        HashSet values = new HashSet();

        for (Iterator val = stopWordFrequencies.values().iterator();
                val.hasNext();) {
            Integer v = (Integer) val.next();

            if (v.intValue() == maxStopWordFrequency) {
                if (!values.contains(v)) {
                    values.add(v);
                } else {
                    draw = true;

                    break;
                }
            }
        }

        return (draw ? MultilingualClusteringContext.UNIDENTIFIED_LANGUAGE_NAME
                     : language);
    }

    /**
     * Tokenizes the input text and returns a "cleaned" version containing only
     * recognizable tokens  and sequence markers.
     */
    private String tokenizeAndClean(String text, Tokenizer tokenizer) {
        StringBuffer stringBuffer = new StringBuffer(text.length());

        tokenizer.restartTokenizerOn(text);

        int[] tokenType = { 0 };
        String tokenImage;
        String tokenImageLowerCase;
        int lastAddedType = Tokenizer.TYPE_SENTENCEMARKER;

        while ((tokenImage = tokenizer.getNextToken(tokenType)) != null) {
            tokenImageLowerCase = tokenImage.toLowerCase();

            switch (tokenType[0]) {
            case Tokenizer.TYPE_PERSON:

                // Pick the last contiguous component of a person's name.
                int i = tokenImage.length() - 1;
outerLoop: 
                while (i >= 0) {
                    switch (tokenImage.charAt(i)) {
                    case ' ':
                    case '.':
                    case '\'': // O'Brian -- maybe we should skip this?
                        i++;

                        break outerLoop;

                    default:}

                    i--;
                }

                tokenImage = tokenImage.substring(i);
                
                break;

            case Tokenizer.TYPE_TERM:

                Object previousTokenImage = caseCheck.get(tokenImageLowerCase);

                if (previousTokenImage == null) {
                    caseCheck.put(tokenImageLowerCase, tokenImage);
                } else {
                    if (!tokenImage.equals(previousTokenImage)) {
                        lowCaseWords.add(tokenImageLowerCase);
                    }
                }

                if (lastAddedType == Tokenizer.TYPE_TERM) {
                    stringBuffer.append(' ');
                }

                stringBuffer.append(tokenImage);
                lastAddedType = Tokenizer.TYPE_TERM;

                break;

            case Tokenizer.TYPE_EMAIL:
            case Tokenizer.TYPE_URL:

                // ignore these.
                break;

            case Tokenizer.TYPE_SENTENCEMARKER:

                if (lastAddedType != Tokenizer.TYPE_SENTENCEMARKER) {
                    stringBuffer.append(" . ");
                    lastAddedType = Tokenizer.TYPE_SENTENCEMARKER;
                }

            default:

                // ignore unknown.
                break;
            }
        }

        return stringBuffer.toString();
    }
}
