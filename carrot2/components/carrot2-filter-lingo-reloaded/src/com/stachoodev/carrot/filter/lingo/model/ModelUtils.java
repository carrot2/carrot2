/*
 * ModelUtils.java Created on 2004-05-22
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.util.common.*;

/**
 * @author stachoo
 */
public class ModelUtils
{
    /**
     * @param tokenSequence
     * @param frequencyMap
     */
    public static void addToFrequencyMap(TokenSequence tokenSequence,
        String propertyName, Map frequencyMap, double multiplier,
        Set tokensUsed, short filterMask)
    {
        // Count token frequencies
        for (int i = 0; i < tokenSequence.getLength(); i++)
        {
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(i);

            // Get token's stem, or its original image when stem is not
            // available
            String stem = null;
            if (token instanceof StemmedToken)
            {
                stem = ((StemmedToken) token).getStem();
            }
            if (stem == null)
            {
                stem = token.toString();
            }

            if ((token.getType() & filterMask) != 0)
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

                    // Stemmed token
                    ExtendedToken originalExtendedToken;
                    Map originalExtendedTokens = (Map) ((ExtendedToken) frequencyMap
                        .get(stem))
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
                            originalExtendedToken
                                .getDoubleProperty(propertyName)
                                + multiplier);
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
                    .getDoubleProperty(propertyName)
                    + multiplier);

                // Add/increase frequency of the original token
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
                        originalExtendedToken.getDoubleProperty(propertyName)
                            + multiplier);
                }
            }
            else
            {
                ExtendedToken extendedToken;

                extendedToken = new ExtendedToken(new TokenStem(token));
                extendedToken.setDoubleProperty(propertyName, multiplier);
                frequencyMap.put(stem, extendedToken);

                // Create a temporary hash map for counting original terms
                Map originalExtendedTokens = new HashMap();
                ExtendedToken originalExtendedToken = new ExtendedToken(token);
                originalExtendedToken.setDoubleProperty(propertyName,
                    multiplier);
                originalExtendedTokens.put(token.toString(),
                    originalExtendedToken);
                extendedToken.setProperty(
                    ExtendedToken.PROPERTY_ORIGINAL_TOKENS,
                    originalExtendedTokens);
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
     */
    public static void addToFrequencyMap(TokenSequence tokenSequence,
        String propertyName, Map frequencyMap, double multiplier,
        short filterMask)
    {
        addToFrequencyMap(tokenSequence, propertyName, frequencyMap,
            multiplier, null, filterMask);
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
            while (((ExtendedToken) list.get(index))
                .getDoubleProperty(frequencyProperty) >= frequencyThreshold)
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
}