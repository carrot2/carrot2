/*
 * ModelUtils.java Created on 2004-05-22
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.*;
import org.apache.commons.collections.map.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

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

            if (tokensUsed != null && tokensUsed.contains(stem))
            {
                continue;
            }

            if (frequencyMap.containsKey(stem))
            {
                ExtendedToken extendedToken = (ExtendedToken) frequencyMap
                        .get(stem);

                extendedToken.setDoubleProperty(propertyName, extendedToken
                        .getDoubleProperty(propertyName)
                        + multiplier);
            }
            else
            {
                ExtendedToken extendedToken;

                extendedToken = new ExtendedToken(new TokenStem(token));
                extendedToken.setDoubleProperty(propertyName, multiplier);
                frequencyMap.put(stem, extendedToken);
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
            String propertyName, Map frequencyMap, double multiplier)
    {
        addToFrequencyMap(tokenSequence, propertyName, frequencyMap,
                multiplier, null, (short) 0);
    }

    /**
     * Converts a list of tokens to an {@link OrderedMap}, in which tokens are
     * values
     * 
     * @param tokenList
     * @return
     */
    public static OrderedMap convertTokenList(List tokenList)
    {
        OrderedMap orderedMap = new LinkedMap();

        return orderedMap;
    }
}