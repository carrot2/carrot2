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
import com.stachoodev.util.common.*;

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
 * TODO: the algorithm not quite works for phrases of length 1 
 * TODO: for some reason not all sequences have non-null original sentences property
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SAPhraseExtractionStrategy implements PhraseExtractionStrategy
{
    /** A suffix array for the input documents */
    private DualLcpSuffixArray dualLcpSuffixArray;

    /** A reversed suffix array for the input documents */
    private DualLcpSuffixArray dualLcpSuffixArrayReversed;

    /** An int wrapper for the input documents */
    private TokenizedDocumentsIntWrapper documentsIntWrapper;

    /** A reversed int wrapper for the input documents */
    private TokenizedDocumentsIntWrapper documentsIntWrapperReversed;

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.filter.lingo.model.PhraseExtractionStrategy#getExtractedPhrases(java.util.List)
     */
    public List getExtractedPhrases(List tokenizedDocuments)
    {
        // Create wrappers
        documentsIntWrapper = new TokenizedDocumentsIntWrapper(
            tokenizedDocuments);
        documentsIntWrapperReversed = new TokenizedDocumentsIntWrapper(
            tokenizedDocuments);
        documentsIntWrapperReversed.reverse();

        // Suffix sort
        DualLcpSuffixSortingStrategy dualLcpSuffixSortingStrategy = new QSDualLcpSuffixSortingStrategy();
        dualLcpSuffixArray = dualLcpSuffixSortingStrategy
            .dualLcpSuffixSort(documentsIntWrapper);
        dualLcpSuffixArrayReversed = dualLcpSuffixSortingStrategy
            .dualLcpSuffixSort(documentsIntWrapperReversed);

        // Extract
        Substring [] substrings = extractFeatures();

        // Convert substrings to TokenSequences
        int [] intArray = documentsIntWrapper.asIntArray();
        List tokenSequences = new ArrayList(substrings.length);
        for (int i = 0; i < substrings.length; i++)
        {
            // Convert integer codes to tokens
            MutableTokenSequence tokenSequence = new MutableTokenSequence();
            for (int t = substrings[i].getFrom(); t < substrings[i].getTo(); t++)
            {
                tokenSequence.addToken(documentsIntWrapper
                    .getTokenStemForCode(intArray[t]));
            }

            // Add frequency information
            ExtendedTokenSequence extendedTokenSequence = new ExtendedTokenSequence(
                tokenSequence);
            extendedTokenSequence
                .setDoubleProperty(ExtendedTokenSequence.PROPERTY_TF,
                    substrings[i].getFrequency());

            // Add original phrases
            List originalPhrases = new ArrayList();
            List secondarySubstringsList = substrings[i]
                .getSecondarySubstrings(substrings[i].length());
            if (secondarySubstringsList != null)
            {
                for (Iterator secondarySubstrings = secondarySubstringsList
                    .iterator(); secondarySubstrings.hasNext();)
                {
                    Substring substring = (Substring) secondarySubstrings
                        .next();

                    // Convert integer codes to tokens
                    MutableTokenSequence originalTokenSequence = new MutableTokenSequence();
                    for (int t = substring.getFrom(); t < substring.getTo(); t++)
                    {
                        originalTokenSequence.addToken(documentsIntWrapper
                            .getTokenForCode(intArray[t]));
                    }

                    // Add frequency information
                    ExtendedTokenSequence originalExtendedTokenSequence = new ExtendedTokenSequence(
                        originalTokenSequence);
                    originalExtendedTokenSequence.setDoubleProperty(
                        ExtendedTokenSequence.PROPERTY_TF, substring
                            .getFrequency());

                    originalPhrases.add(originalExtendedTokenSequence);
                }

                // Sort the original phrases according to frequency
                Collections.sort(originalPhrases, PropertyHelper
                    .getComparatorForDoubleProperty(
                        ExtendedTokenSequence.PROPERTY_TF, true));

                extendedTokenSequence.setProperty(
                    ExtendedTokenSequence.PROPERTY_ORIGINAL_TOKEN_SEQUENCES,
                    originalPhrases);
            }

            tokenSequences.add(extendedTokenSequence);
        }

        // Sort the phrases according to frequency
        Collections.sort(tokenSequences, PropertyHelper
            .getComparatorForDoubleProperty(ExtendedTokenSequence.PROPERTY_TF,
                true));

        return tokenSequences;
    }

    /**
     * Discovers complete substrings in the input documents. Note: the returned
     * array is not sorted.
     * 
     * @return
     */
    private Substring [] extractFeatures()
    {
        // Find RCS and LCS
        RcsDiscoveryStrategy rcsDiscoveryStrategy = new RcsDiscoveryStrategy();
        Substring [] rcs = rcsDiscoveryStrategy.discoverRcs(dualLcpSuffixArray,
            documentsIntWrapper);
        Substring [] lcs = rcsDiscoveryStrategy.discoverRcs(
            dualLcpSuffixArrayReversed, documentsIntWrapperReversed);

        SubstringComparator comparator = new SubstringComparator(
            documentsIntWrapper);

        // Sort the RCS alphabetically
        Arrays.sort(rcs, comparator);

        // Reverse the LCS again (to make the substrings plain)
        for (int i = 0; i < lcs.length; i++)
        {
            lcs[i].reverse(documentsIntWrapper.length());
        }

        // Sort the LCS alphabetically
        Arrays.sort(lcs, comparator);

        // Intersect the RSC and LCS
        int i = 0;
        int j = 0;
        List result = new ArrayList();

        while ((i < lcs.length) && (j < rcs.length))
        {
            Substring l = lcs[i];
            Substring r = rcs[j];

            if (comparator.compare(l, r) == 0)
            {
                if (rcs[j].length() > 1) // extract phrases longer than 1
                {
                    result.add(rcs[j]);
                }

                i++;
                j++;

                continue;
            }

            if (comparator.compare(l, r) < 0)
            {
                i++;

                continue;
            }

            if (comparator.compare(l, r) > 0)
            {
                j++;

                continue;
            }
        }

        Substring [] completeSubstrings = (Substring []) result
            .toArray(new Substring [result.size()]);

        return completeSubstrings;
    }

    /** Encapsulates the RCS discovery */
    private class RcsDiscoveryStrategy
    {
        private int [] lcpArray;
        private int [] superLcpArray;
        private int [] suffixArray;

        private Substring [] superRcsStack;
        private int superSp;
        private Substring [] rcsStack;
        private int sp;

        private int i;

        /**
         * Discovers Right Complete Substrings in the given LCP Suffix Array.
         * 
         * @return Substring [] the discovered Right Complete substrings
         */
        private Substring [] discoverRcs(DualLcpSuffixArray dualLcpSuffixArray,
            TokenizedDocumentsIntWrapper snippetsIntWrapper)
        {
            if (dualLcpSuffixArray.getSuffixArray().length == 0)
            {
                return new Substring [0];
            }

            lcpArray = dualLcpSuffixArray.getLcpArray();
            superLcpArray = dualLcpSuffixArray.getSuperLcpArray();
            suffixArray = dualLcpSuffixArray.getSuffixArray();

            // The super RCS stack stores the generalized (stemmed) phrases
            superRcsStack = new Substring [superLcpArray.length];
            superSp = -1;

            // The RCS stack stores the original (not stemmed) phrases
            rcsStack = new Substring [superLcpArray.length];
            sp = -1;

            i = 1;

            List result = new ArrayList();
            while (i < superLcpArray.length)
            {
                if (superSp < 0)
                {
                    if (superLcpArray[i] > 0)
                    {
                        superSp++;
                        superRcsStack[superSp] = new Substring(i,
                            suffixArray[i], suffixArray[i] + superLcpArray[i],
                            2);

                        // superSp < 0 implies sp < 0 ?
                        if (lcpArray[i] > 0)
                        {
                            sp++;
                            rcsStack[sp] = new Substring(i, suffixArray[i],
                                suffixArray[i] + lcpArray[i], 2);
                        }

                        // Attach the original phrases
                        for (int l = superLcpArray[i]; l > lcpArray[i]; l--)
                        {
                            superRcsStack[superSp].addSecondarySubstring(l,
                                new Substring(i - 1, suffixArray[i - 1],
                                    suffixArray[i - 1] + l, 1));
                        }
                    }

                    i++;
                }
                else
                {
                    int r = superRcsStack[superSp].getId();

                    if (superLcpArray[r] < superLcpArray[i])
                    {
                        // Add a new generalized phrase to the super stack
                        superSp++;
                        superRcsStack[superSp] = new Substring(i,
                            suffixArray[i], suffixArray[i] + superLcpArray[i],
                            2);

                        updatePrimary(false);

                        i++;
                    }
                    else if (superLcpArray[r] == superLcpArray[i])
                    {
                        // Increase the frequency of the generalized phrase
                        superRcsStack[superSp].increaseFrequency(1);

                        updatePrimary(false);

                        i++;
                    }
                    else
                    {
                        Substring s;

                        updatePrimary(true);

                        // Pop generalized phrases off the stack
                        do
                        {
                            // Add the generalized phrase to the result
                            result.add(superRcsStack[superSp]);

                            s = superRcsStack[superSp];
                            superSp--;

                            // This fragment must be here in order for the
                            // algorithm to work properly. It handles the case
                            // where superRcsStack[superSp] < lcpArray[i] <
                            // s.length. We need to put the current substring on
                            // the stack now (and not in the next iteration, as
                            // it was done in the original version) so that it
                            // 'inherits' the frequency and original phrases
                            // properly.
                            int len = (superSp < 0 ? 0 : superRcsStack[superSp]
                                .length());
                            if (superLcpArray[i] > len)
                            {
                                superSp++;
                                superRcsStack[superSp] = new Substring(i,
                                    suffixArray[i], suffixArray[i]
                                        + superLcpArray[i], 2);
                            }

                            // As we update only the frequency of the stack's
                            // topmost substring we need to propagate the
                            // accummulated frequencies to the shorter
                            // substrings
                            if (superSp >= 0)
                            {
                                superRcsStack[superSp].increaseFrequency(s
                                    .getFrequency() - 1);

                                // Propagate original phrases not longer than
                                // the generalized phrase on the stack
                                for (int l = superRcsStack[superSp].length(); l > 0; l--)
                                {
                                    List substrings = s
                                        .removeSecondarySubstrings(l);
                                    if (substrings != null)
                                    {
                                        superRcsStack[superSp]
                                            .addSecondarySubstrings(l,
                                                substrings);
                                    }
                                }

                                // Remove from the generalized phrase the
                                // original phrases that are shorter than the
                                // generalized phrase's length
                                for (int l = superRcsStack[superSp].length() + 1; l < s
                                    .length(); l++)
                                {
                                    s.removeSecondarySubstrings(l);
                                }
                            }
                        }
                        while (superSp > 0
                            && superLcpArray[superRcsStack[superSp].getId()] > superLcpArray[i]);

                        i++;
                    }
                }
            }

            return (Substring []) result.toArray(new Substring [result.size()]);
        }

        /**
         *  
         */
        private void updatePrimary(boolean caseC)
        {
            // This hadles the minor differene between cases AB and C
            int x = (caseC ? 1 : 0);

            // The rcsStack may still be empty at this point
            if (sp < 0)
            {
                if (lcpArray[i] > 0)
                {
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                        suffixArray[i] + lcpArray[i], 2);
                }

                // Attach the original phrases
                for (int l = superLcpArray[i - x]; l > lcpArray[i]; l--)
                {
                    superRcsStack[superSp].addSecondarySubstring(l,
                        new Substring(i - 1, suffixArray[i - 1],
                            suffixArray[i - 1] + l, 1));
                }
            }
            else
            {
                int s = rcsStack[sp].getId();

                if (lcpArray[s] < lcpArray[i])
                {
                    sp++;
                    rcsStack[sp] = new Substring(i, suffixArray[i],
                        suffixArray[i] + lcpArray[i], 2);

                    // Attach original phrases that appear only once to the
                    // generalized phrase. The length at which the phrase
                    // becomes unique is given by lcpArray[i]. Add all variants
                    // that have length between lcpArray[i] and superLcpArray[i]
                    // to the generalized substring (these appear only once).
                    // Substrings shorter than lcpArray[i] will be propagated to
                    // shorter generalized phrases while popping off the
                    // superRcsStack).
                    for (int l = superLcpArray[i - x]; l > lcpArray[i]; l--)
                    {
                        superRcsStack[superSp].addSecondarySubstring(l,
                            new Substring(i - 1, suffixArray[i - 1],
                                suffixArray[i - 1] + l, 1));
                    }
                }
                else if (lcpArray[s] == lcpArray[i])
                {
                    rcsStack[sp].increaseFrequency(1);

                    for (int l = superLcpArray[i - x]; l > lcpArray[i]; l--)
                    {
                        superRcsStack[superSp].addSecondarySubstring(l,
                            new Substring(i - 1, suffixArray[i - 1],
                                suffixArray[i - 1] + l, 1));
                    }
                }
                else
                {
                    // Note this loop has lcpArray[i - 1] instead
                    // of lcpArray[i] as in previous cases.
                    for (int l = superLcpArray[i - x]; l > lcpArray[i - 1]; l--)
                    {
                        superRcsStack[superSp].addSecondarySubstring(l,
                            new Substring(i - 1, suffixArray[i - 1],
                                suffixArray[i - 1] + l, 1));
                    }

                    Substring ss;

                    // Loop to pop all substrings from the rcsStack, so that the
                    // length of the substring on the stack is not greater that
                    // the length of the current LCP
                    do
                    {
                        ss = rcsStack[sp];
                        sp--;

                        // Analogous to the generalized phrase case
                        int len = (sp < 0 ? 0 : rcsStack[sp].length());
                        if (lcpArray[i] > len)
                        {
                            sp++;
                            rcsStack[sp] = new Substring(i, suffixArray[i],
                                suffixArray[i] + lcpArray[i], 2);
                        }

                        // Attach the original phrase that appeared
                        // more than once to the generalized substring
                        len = (sp < 0 ? 0 : rcsStack[sp].length());
                        for (int l = ss.length(); l > len; l--)
                        {
                            superRcsStack[superSp].addSecondarySubstring(l,
                                new Substring(ss.getId(), ss.getFrom(), ss
                                    .getFrom()
                                    + l, ss.getFrequency()));
                        }

                        // Propagate/accummulate frequencies
                        if (sp >= 0)
                        {
                            rcsStack[sp]
                                .increaseFrequency(ss.getFrequency() - 1);

                        }
                    }
                    while (sp > 0
                        && lcpArray[rcsStack[sp].getId()] > lcpArray[i]);
                }
            }
        }

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

        /**
         * A map of secondary (not stemmed) substrings. Keys are Integers
         * representing the length of the substring, values are Lists of
         * substrings of the corresponding length
         */
        private Map secondarySubstrings;

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
         * @param substring
         */
        public void addSecondarySubstring(int length, Substring substring)
        {
            // Note: we do NOT aim to make this class thread-safe, hence no
            // synchronization
            if (secondarySubstrings == null)
            {
                secondarySubstrings = new HashMap();
            }

            Integer len = new Integer(length);
            if (secondarySubstrings.get(len) != null)
            {
                List substrings = (List) secondarySubstrings.get(len);
                substrings.add(substring);
            }
            else
            {
                List substrings = new ArrayList();
                secondarySubstrings.put(len, substrings);
                substrings.add(substring);
            }
        }

        /**
         * @param length
         * @return
         */
        public List removeSecondarySubstrings(int length)
        {
            if (secondarySubstrings != null)
            {
                return (List) secondarySubstrings.remove(new Integer(length));
            }
            else
            {
                return null;
            }
        }

        /**
         * @param length
         * @param substrings
         */
        public void addSecondarySubstrings(int length, List substrings)
        {
            // Note: we do NOT aim to make this class thread-safe, hence no
            // synchronization
            if (secondarySubstrings == null)
            {
                secondarySubstrings = new HashMap();
            }

            Integer len = new Integer(length);
            if (secondarySubstrings.get(len) != null)
            {
                ((List) secondarySubstrings.get(len)).addAll(substrings);
            }
            else
            {
                secondarySubstrings.put(len, substrings);
            }
        }

        /**
         * @return
         */
        public List getSecondarySubstrings(int length)
        {
            if (secondarySubstrings != null)
            {
                return (List) secondarySubstrings.get(new Integer(length));
            }
            else
            {
                return null;
            }
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