/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.suffixarrays.*;
import com.stachoodev.suffixarrays.wrapper.*;

/**
 * Discovers frequent phrases (sequences of words longer than one word appearing
 * in the text more than once) in the input
 * {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}s using
 * an enhanced version of the algorithm presented in "Zhang and Dong 04,
 * Semantic, Hierarchical, Online Clustering of Web Search Results Proceedings
 * of the 6th Asia Pacific Web Conference (APWEB), Hangzhou, China, April 2004".
 * The enhancements are:
 * 
 * <ul>
 * <li>if the input documents are stemmed, the algorithm returns <i>generalized
 * frequent phrases </i>, i.e. phrases consisting of stems, along with the
 * corresponding original phrases and their frequencies. A list of original
 * phrases is stored under the
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.ExtendedTokenSequence#PROPERTY_ORIGINAL_TOKEN_SEQUENCES}
 * property of the generalized phrase.
 * <li>if stop words information is present the returned phrases will be
 * trimmed of the trailing stop words
 * <li>phrase frequencies are correctly calculated
 * </ul>
 * 
 * Note: This class is <b>not </b> thread-safe. <br>
 * Note: This class is <b>not </b> multilingual. Expect strange results when
 * applied to multilingual data (i.e. tokens with inconsistent stopword markers)
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SAPhraseExtraction extends PhraseExtractionBase implements
    PhraseExtraction
{
    /** A suffix array for the input documents */
    private DualLcpSuffixArray dualLcpSuffixArray;

    /** A reversed suffix array for the input documents */
    private DualLcpSuffixArray dualLcpSuffixArrayReversed;

    /** An int wrapper for the input documents */
    private TokenizedDocumentsIntWrapper documentsIntWrapper;

    /** A reversed int wrapper for the input documents */
    private TokenizedDocumentsIntWrapper documentsIntWrapperReversed;

    /** */
    private SubstringComparator substringComparator;

    /** */
    public static final String PROPERTY_MAX_PHRASE_LENGTH = "mpl";
    private static final int DEFAULT_MAX_PHRASE_LENGTH = 7;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.PhraseExtractionStrategy#getExtractedPhrases(java.util.List)
     */
    public List getExtractedPhrases(ModelBuilderContext context)
    {
        // Create wrappers
        documentsIntWrapper = context.getIntWrapper();
        documentsIntWrapperReversed = context.getIntWrapperReversed();

        // Suffix sort
        dualLcpSuffixArray = context.getSuffixArray();
        dualLcpSuffixArrayReversed = context.getSuffixArrayReversed();

        substringComparator = new SubstringComparator(documentsIntWrapper);

        // Extract generalised phrases and add most frequent original phrases
        // NB: these processes can be parallelized
        Substring [] substrings = extractFeatures();
        addOriginalPhrases(substrings);

        // Convert substrings to TokenSequences
        int [] intData = documentsIntWrapper.asIntArray();
        List tokenSequences = new ArrayList(substrings.length);
        List extractedPhraseCodes = new ArrayList(substrings.length);
        for (int i = 0; i < substrings.length; i++)
        {
            // Convert integer codes to tokens
            MutableTokenSequence tokenSequence = new MutableTokenSequence();
            int [] phraseCodes = new int [substrings[i].length()];
            for (int t = substrings[i].getFrom(); t < substrings[i].getTo(); t++)
            {
                tokenSequence.addToken(documentsIntWrapper
                    .getTokenStemForCode(intData[t]));
                phraseCodes[t - substrings[i].getFrom()] = intData[t];
            }
            extractedPhraseCodes.add(phraseCodes);

            // Add frequency information
            ExtendedTokenSequence extendedTokenSequence = new ExtendedTokenSequence(
                tokenSequence);
            extendedTokenSequence
                .setDoubleProperty(ExtendedTokenSequence.PROPERTY_TF,
                    substrings[i].getFrequency());

            // Add information about the most frequent original token
            Substring originalSubstring = substrings[i].getOriginalSubstring();

            // Convert integer codes to tokens
            if (originalSubstring != null)
            {
                MutableTokenSequence originalTokenSequence = new MutableTokenSequence();
                for (int t = originalSubstring.getFrom(); t < originalSubstring
                    .getTo(); t++)
                {
                    originalTokenSequence.addToken(documentsIntWrapper
                        .getTokenForCode(intData[t]));
                }

                extendedTokenSequence
                    .setProperty(
                        ExtendedTokenSequence.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN_SEQUENCE,
                        originalTokenSequence);
            }

            tokenSequences.add(extendedTokenSequence);
        }

        // Sort the phrases according to frequency
        //        Collections.sort(tokenSequences, PropertyHelper
        //            .getComparatorForDoubleProperty(ExtendedTokenSequence.PROPERTY_TF,
        //                true));
        context.setExtractedPhraseCodes(extractedPhraseCodes);

        return tokenSequences;
    }

    /**
     * @param substrings
     */
    private void addOriginalPhrases(Substring [] substrings)
    {
        Substring [] rcs = discoverRcs(dualLcpSuffixArray.getSuffixArray(),
            dualLcpSuffixArray.getLcpArray(), documentsIntWrapper);

        // Sort the substrings first so that we can do binary searches
        Arrays.sort(substrings, substringComparator);

        // Add original phrases that appeared more than once
        for (int i = 0; i < rcs.length; i++)
        {
            int index = Arrays.binarySearch(substrings, rcs[i],
                substringComparator);

            if (index >= 0)
            {
                substrings[index].updateOriginalSubstring(rcs[i]);
            }
        }

        // Add original phrases that have only one representative
        for (int i = 0; i < substrings.length; i++)
        {
            if (substrings[i].getOriginalSubstring() == null)
            {
                Substring originalSubstring = firstIndexOf(substrings[i]);

                if (originalSubstring != null)
                {
                    substrings[i].updateOriginalSubstring(originalSubstring);
                }
                else
                {
                    throw new RuntimeException("Ooops?");
                }
            }
        }

    }

    /**
     * @param substring
     * @return
     */
    private Substring firstIndexOf(Substring substring)
    {
        int [] suffixArray = dualLcpSuffixArray.getSuffixArray();
        int [] intData = documentsIntWrapper.asIntArray();
        Substring result = null;

        // As we're searching the non-masked array using a masked code, we
        // cannoty simply use a binary search and compare full substrings.
        // Instead, once we've found a position that after masking matches the
        // first word of the substring, we need to switch to linear search
        // up and down the suffix array. We cannot continue binary search
        // as the sought masked code gives us no clue as to to which half
        // we should jump.

        // Use binarch search to narrow down the possible search area
        int left = 0;
        int right = suffixArray.length;
        int pos;
        int compared;
        do
        {
            pos = (left + right) / 2;

            compared = SuffixArraysUtils.compareSuffixes(suffixArray[pos],
                substring.getFrom(), 1, intData,
                MaskableIntWrapper.SECONDARY_BITS);
            if (compared == 0)
            {
                break;
            }
            else if (compared < 0)
            {
                left = pos + 1;
            }
            else
            {
                right = pos;
            }
        }
        while (left != right);

        int posBackup = pos;

        // Linear search up the suffix array
        do
        {
            if (SuffixArraysUtils.compareSuffixes(suffixArray[pos], substring
                .getFrom(), substring.length(), intData,
                MaskableIntWrapper.SECONDARY_BITS) == 0)
            {
                result = new Substring(pos, suffixArray[pos], suffixArray[pos]
                    + substring.length(), -1);
                return result;
            }
            pos--;
        }
        while (SuffixArraysUtils.compareSuffixes(suffixArray[pos], substring
            .getFrom(), 1, intData, MaskableIntWrapper.SECONDARY_BITS) == 0);

        // Linear search down the suffix array
        pos = posBackup + 1;
        while (SuffixArraysUtils.compareSuffixes(suffixArray[pos], substring
            .getFrom(), 1, intData, MaskableIntWrapper.SECONDARY_BITS) == 0)
        {
            if (SuffixArraysUtils.compareSuffixes(suffixArray[pos], substring
                .getFrom(), substring.length(), intData,
                MaskableIntWrapper.SECONDARY_BITS) == 0)
            {
                result = new Substring(pos, suffixArray[pos], suffixArray[pos]
                    + substring.length(), -1);
                return result;
            }
            pos++;
        }

        return result;
    }

    /**
     * Discovers complete substrings in the input documents. Note: the returned
     * array is not sorted.
     * 
     * @return
     */
    private Substring [] extractFeatures()
    {
        Substring [] rcs = discoverRcs(
            dualLcpSuffixArray.getSuperSuffixArray(), dualLcpSuffixArray
                .getSuperLcpArray(), documentsIntWrapper);
        Substring [] lcs = discoverRcs(dualLcpSuffixArrayReversed
            .getSuperSuffixArray(), dualLcpSuffixArrayReversed
            .getSuperLcpArray(), documentsIntWrapperReversed);

        int maxPhraseLength = getIntProperty(PROPERTY_MAX_PHRASE_LENGTH,
            DEFAULT_MAX_PHRASE_LENGTH);

        // Sort the RCS alphabetically
        // TODO: do we really need that?
        Arrays.sort(rcs, substringComparator);

        // Reverse the LCS again (to make the substrings plain)
        for (int i = 0; i < lcs.length; i++)
        {
            lcs[i].reverse(documentsIntWrapper.length());
        }

        // Sort the LCS alphabetically
        Arrays.sort(lcs, substringComparator);

        // Intersect the RSC and LCS
        int i = 0;
        int j = 0;
        List result = new ArrayList();

        while ((i < lcs.length) && (j < rcs.length))
        {
            Substring l = lcs[i];
            Substring r = rcs[j];

            if (substringComparator.compare(l, r) == 0)
            {
                // extract phrases longer than 1
                if (rcs[j].length() >= 2 && rcs[j].length() <= maxPhraseLength)
                {
                    result.add(rcs[j]);
                }

                i++;
                j++;

                continue;
            }

            if (substringComparator.compare(l, r) < 0)
            {
                i++;

                continue;
            }

            if (substringComparator.compare(l, r) > 0)
            {
                j++;

                continue;
            }
        }

        Substring [] completeSubstrings = (Substring []) result
            .toArray(new Substring [result.size()]);

        return completeSubstrings;
    }

    /**
     * Discovers Right Complete Substrings in the given LCP Suffix Array.
     * 
     * @return Substring [] the discovered Right Complete substrings
     */
    private Substring [] discoverRcs(int [] suffixArray, int [] lcpArray,
        TokenizedDocumentsIntWrapper snippetsIntWrapper)
    {
        Substring [] rcsStack;
        int sp;

        int i;

        if (suffixArray.length == 0)
        {
            return new Substring [0];
        }

        // The super RCS stack stores the generalized (stemmed) phrases
        rcsStack = new Substring [lcpArray.length];
        sp = -1;

        i = 1;

        List result = new ArrayList();
        while (i < lcpArray.length)
        {
            if (sp < 0)
            {
                if (lcpArray[i] > 0)
                {
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                        suffixArray[i] + lcpArray[i], 2);
                }

                i++;
            }
            else
            {
                int r = rcsStack[sp].getId();

                if (lcpArray[r] < lcpArray[i])
                {
                    // Add a new generalized phrase to the super stack
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                        suffixArray[i] + lcpArray[i], 2);

                    i++;
                }
                else if (lcpArray[r] == lcpArray[i])
                {
                    // Increase the frequency of the generalized phrase
                    rcsStack[sp].increaseFrequency(1);

                    i++;
                }
                else
                {
                    Substring s;

                    // Pop generalized phrases off the stack
                    do
                    {
                        // Add the generalized phrase to the result
                        result.add(rcsStack[sp]);

                        s = rcsStack[sp];
                        sp--;

                        // This fragment must be here in order for the
                        // algorithm to work properly. It handles the case
                        // where rcsStack[sp] < lcpArray[i] <
                        // s.length. We need to put the current substring on
                        // the stack now (and not in the next iteration, as
                        // it was done in the original version) so that it
                        // 'inherits' the frequency and original phrases
                        // properly.
                        int len = (sp < 0 ? 0 : rcsStack[sp].length());
                        if (lcpArray[i] > len)
                        {
                            sp++;
                            rcsStack[sp] = new Substring(i, suffixArray[i],
                                suffixArray[i] + lcpArray[i], 2);
                        }

                        // As we update only the frequency of the stack's
                        // topmost substring we need to propagate the
                        // accummulated frequencies to the shorter
                        // substrings
                        if (sp >= 0)
                        {
                            rcsStack[sp]
                                .increaseFrequency(s.getFrequency() - 1);
                        }
                    }
                    while (sp > 0
                        && lcpArray[rcsStack[sp].getId()] > lcpArray[i]);

                    i++;
                }
            }
        }

        return (Substring []) result.toArray(new Substring [result.size()]);
    }

    /**
     *  
     */
    public void clear()
    {
        documentsIntWrapper = null;
        documentsIntWrapperReversed = null;
        dualLcpSuffixArray = null;
        dualLcpSuffixArrayReversed = null;
    }

    /**
     * Represents a general substring. Contains information on the substring's
     * boundaries and absolute frequency.
     */
    private class Substring implements Comparable
    {
        /** The substring's unique id */
        private int id;

        /** Substring's start position */
        private int from;

        /** Substring's end position */
        private int to;

        /** Substring's absoulte frequency */
        private int frequency;

        /** Most frequent original substring */
        private Substring originalSubstring;

        /**
         * @param id
         * @param from
         * @param to
         * @param frequency
         */
        public Substring(int id, int from, int to, int frequency)
        {
            this.id = id;
            this.from = from;
            this.to = to;
            this.frequency = frequency;
        }

        /**
         * @param newOriginalSubstring
         */
        public void updateOriginalSubstring(Substring newOriginalSubstring)
        {
            if (originalSubstring == null
                || newOriginalSubstring.frequency > originalSubstring.frequency)
            {
                originalSubstring = newOriginalSubstring;
            }
        }

        /**
         * Returns the most frequent original substring.
         * 
         * @return
         */
        public Substring getOriginalSubstring()
        {
            return originalSubstring;
        }

        /**
         * @return
         */
        public int getId()
        {
            return id;
        }

        /**
         * @return
         */
        public int getFrom()
        {
            return from;
        }

        /**
         * @return
         */
        public int getTo()
        {
            return to;
        }

        /**
         * @return
         */
        public int length()
        {
            return to - from;
        }

        /**
         * @param length
         */
        public void reverse(int length)
        {
            int oldFrom = from;
            from = length - to;
            to = length - oldFrom;
        }

        /**
         * @return
         */
        public int getFrequency()
        {
            return frequency;
        }

        /**
         * @param frequency
         */
        public void setFrequency(int frequency)
        {
            this.frequency = frequency;
        }

        /**
         * @param increment
         */
        public void increaseFrequency(int increment)
        {
            this.frequency += increment;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o)
        {
            if (o == this)
            {
                return true;
            }

            if (o == null)
            {
                return false;
            }

            if (!(getClass() != o.getClass())
                || (((Substring) o).getId() != id))
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            return id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object obj)
        {
            if (!(getClass() != obj.getClass()))
            {
                throw new ClassCastException(obj.getClass().toString());
            }

            if (id < ((Substring) obj).getId())
            {
                return -1;
            }
            else if (id > ((Substring) obj).getId())
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return "[" + id + " " + from + " " + to + " " + frequency + "]";
        }
    }

    /**
     * Compares instances of the Substring class referring to given IntWrapper
     * instance.
     */
    private class SubstringComparator implements Comparator
    {
        /** Current wrapper's int data */
        private int [] intData;

        /**
         * Creates a Comparator for substrings relating to the given intWrapper.
         * 
         * @param intWrapper
         */
        public SubstringComparator(IntWrapper intWrapper)
        {
            this.intData = intWrapper.asIntArray();
        }

        /**
         * Compares two substrings. Note: Shorter strings are bigger !
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object s1, Object s2)
        {
            if (!((s1.getClass() == Substring.class) && (s2.getClass() == Substring.class)))
            {
                throw new ClassCastException(s1.getClass().toString());
            }

            int s1From = ((Substring) s1).getFrom();
            int s1To = ((Substring) s1).getTo();
            int s2From = ((Substring) s2).getFrom();
            int s2To = ((Substring) s2).getTo();

            if (((s2To - s2From) == (s1To - s1From)) && ((s2To - s2From) == 0))
            {
                return 0;
            }

            for (int i = 0; i < (((s2To - s2From) < (s1To - s1From)) ? (s2To - s2From)
                : (s1To - s1From)); i++)
            {
                if ((intData[s1From + i] & MaskableIntWrapper.SECONDARY_MASK) < (intData[s2From
                    + i] & MaskableIntWrapper.SECONDARY_MASK))
                {
                    return -1;
                }
                else if ((intData[s1From + i] & MaskableIntWrapper.SECONDARY_MASK) > (intData[s2From
                    + i] & MaskableIntWrapper.SECONDARY_MASK))
                {
                    return 1;
                }
            }

            if ((s2To - s2From) < (s1To - s1From))
            {
                return -1;
            }
            else if ((s2To - s2From) > (s1To - s1From))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}