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
import com.stachoodev.util.common.*;

/**
 * An ugly class that holds a number of intricate "utility" routined for
 * clustering models used by Lingo.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ModelUtils
{
    /**
     * @param tokenSequence
     * @param frequencyMap
     * @param storeOriginalForms
     */
    public static void addToFrequencyMap(TokenSequence tokenSequence,
        String propertyName, Map frequencyMap, double multiplier,
        Set tokensUsed, short filterMask, boolean storeOriginalForms)
    {
        // Count token frequencies
        for (int i = 0; i < tokenSequence.getLength(); i++)
        {
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(i);

            // Get token's stem, or its original image when stem is not
            // available
            String stem = getStem(token);

            if ((token.getType() & filterMask) != 0
                || token.toString().length() == 1)
            {
                continue;
            }

            // Support for document-frequency counting
            if (tokensUsed != null && tokensUsed.contains(stem))
            {
                // If we have not seen that variant before - store it
                if (!tokensUsed.contains(token.toString()))
                {
                    tokensUsed.add(token.toString());

                    // Store original forms when requested
                    if (storeOriginalForms)
                    {
                        ExtendedToken originalExtendedToken;
                        Map originalExtendedTokens = (Map) ((ExtendedToken) frequencyMap
                            .get(stem))
                            .getProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS);
                        if (!originalExtendedTokens.containsKey(token
                            .toString()))
                        {
                            originalExtendedToken = new ExtendedToken(token);
                            originalExtendedToken.setDoubleProperty(
                                propertyName, multiplier);
                            originalExtendedTokens.put(token.toString(),
                                originalExtendedToken);
                        }
                        else
                        {
                            originalExtendedToken = (ExtendedToken) (originalExtendedTokens)
                                .get(token.toString());
                            originalExtendedToken.setDoubleProperty(
                                propertyName, originalExtendedToken
                                    .getDoubleProperty(propertyName, 0)
                                    + multiplier);
                        }
                    }
                }

                continue;
            }

            if (frequencyMap.containsKey(stem))
            {
                // Stemmed token
                ExtendedToken extendedToken = (ExtendedToken) frequencyMap
                    .get(stem);

                // Increase the frequency of the stemmed token
                extendedToken.setDoubleProperty(propertyName, extendedToken
                    .getDoubleProperty(propertyName, 0)
                    + multiplier);

                // Add/increase frequency of the original token
                if (storeOriginalForms)
                {
                    ExtendedToken originalExtendedToken;
                    Map originalExtendedTokens = (Map) extendedToken
                        .getProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS);
                    if (!originalExtendedTokens.containsKey(token.toString()))
                    {
                        originalExtendedToken = new ExtendedToken(token);
                        originalExtendedToken.setDoubleProperty(propertyName,
                            multiplier);
                        originalExtendedTokens.put(token.toString(),
                            originalExtendedToken);
                    }
                    else
                    {
                        originalExtendedToken = (ExtendedToken) (originalExtendedTokens)
                            .get(token.toString());
                        originalExtendedToken.setDoubleProperty(propertyName,
                            originalExtendedToken.getDoubleProperty(
                                propertyName, 0)
                                + multiplier);
                    }
                }
            }
            else
            {
                ExtendedToken extendedToken;

                extendedToken = new ExtendedToken(new TokenStem(token));
                extendedToken.setDoubleProperty(propertyName, multiplier);
                frequencyMap.put(stem, extendedToken);

                // Create a temporary hash map for counting original terms
                if (storeOriginalForms)
                {
                    Map originalExtendedTokens = new HashMap();
                    ExtendedToken originalExtendedToken = new ExtendedToken(
                        token);
                    originalExtendedToken.setDoubleProperty(propertyName,
                        multiplier);
                    originalExtendedTokens.put(token.toString(),
                        originalExtendedToken);
                    extendedToken.setProperty(
                        ExtendedToken.PROPERTY_ORIGINAL_TOKENS,
                        originalExtendedTokens);
                }
            }

            if (tokensUsed != null)
            {
                tokensUsed.add(stem);
            }
        }
    }

    /**
     * @param tokenSequence
     * @param frequencyMap
     * @param storeOriginalForms TODO
     */
    public static void addToFrequencyMap(TokenSequence tokenSequence,
        String propertyName, Map frequencyMap, double multiplier,
        short filterMask, boolean storeOriginalForms)
    {
        addToFrequencyMap(tokenSequence, propertyName, frequencyMap,
            multiplier, null, filterMask, storeOriginalForms);
    }

    /**
     * Converts a list of tokens to a {@link Map}, in which tokens are values
     * and token.toString() are keys.
     * 
     * @param tokenList
     * @return
     */
    public static Map tokenListAsMap(List tokenList)
    {
        Map orderedMap = new HashMap();

        for (Iterator tokens = tokenList.iterator(); tokens.hasNext();)
        {
            Token token = (Token) tokens.next();
            orderedMap.put(token.toString(), token);
        }

        return orderedMap;
    }

    /**
     * @param tokenFrequencies
     * @return
     */
    public static List frequencyMapToList(HashMap tokenFrequencies,
        String frequencyProperty, double frequencyThreshold)
    {
        // Create the final list
        ArrayList list = new ArrayList(tokenFrequencies.values());
        Comparator comparator = PropertyHelper.getComparatorForDoubleProperty(
            frequencyProperty, true);
        Collections.sort(list, comparator);

        // A fake ExtendedToken we will use to binary-search the token list
        ExtendedToken thresholdToken = new ExtendedToken(null);
        thresholdToken.setDoubleProperty(frequencyProperty, frequencyThreshold);

        // NB: binarySearch requires that the list be sorted ascendingly, but
        // as long the supplied comparator is consistent with the list's
        // ordering the method will work as expected
        int index = Collections.binarySearch(list, thresholdToken, comparator);

        if (index < 0)
        {
            index = -index;
        }
        else
        {
            while (((ExtendedToken) list.get(index)).getDoubleProperty(
                frequencyProperty, 0) >= frequencyThreshold)
            {
                index++;
            }
        }

        List finalList = list.subList(0, index - 1);

        // Add position of the token on the feature list
        // Convert original token hash maps into sorted lists
        int i = 0;
        for (Iterator tokens = finalList.iterator(); tokens.hasNext();)
        {
            ExtendedToken token = (ExtendedToken) tokens.next();

            // Add index
            token.setIntProperty(ExtendedToken.PROPERTY_INDEX, i++);

            // Convert the hashmap into a list
            Map originalTokenMap = (Map) token
                .getProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS);
            List tokenList = new ArrayList(originalTokenMap.values());
            Collections.sort(tokenList, comparator);
            token
                .setProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS, tokenList);
        }

        return finalList;
    }

    /**
     * @param token
     * @return
     */
    public static String getStem(TypedToken token)
    {
        String stem = null;
        if (token instanceof StemmedToken)
        {
            stem = ((StemmedToken) token).getStem();
        }
        if (stem == null)
        {
            stem = token.toString();
        }
        return stem;
    }
}