/*
 * ModelTestUtils.java Created on 2004-06-23
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * @author stachoo
 */
public class ModelTestUtils
{
    /**
     * @param index
     * @param property
     * @param stem
     * @param stemTf
     * @param originalTokens
     * @param originalTf
     * @return
     */
    static ExtendedToken createTokenStem(int index, String property,
        String stem, double stemTf, String [] originalTokens,
        double [] originalTf)
    {
        StringTypedToken token = new StringTypedToken();
        token.assign(stem, (short) 0);
        ExtendedToken extendedTokenStem = new ExtendedToken(
            new TokenStem(token));
        extendedTokenStem.setDoubleProperty(property, stemTf);
        extendedTokenStem.setIntProperty(ExtendedToken.PROPERTY_INDEX, index);

        List originalExtendedTokens = new ArrayList(originalTokens.length);
        for (int i = 0; i < originalTokens.length; i++)
        {
            StringTypedToken originalToken = new StringTypedToken();
            originalToken.assign(originalTokens[i], (short) 0);
            ExtendedToken originalExtendedToken = new ExtendedToken(
                originalToken);
            originalExtendedToken.setDoubleProperty(property, originalTf[i]);
            originalExtendedTokens.add(originalExtendedToken);
        }
        extendedTokenStem.setProperty(ExtendedToken.PROPERTY_ORIGINAL_TOKENS,
            originalExtendedTokens);

        return extendedTokenStem;
    }

    /**
     * @param index
     * @param property
     * @param stem
     * @param stemTf
     * @return
     */
    static ExtendedToken createTokenStem(int index, String property,
        String stem, double stemTf)
    {
        return createTokenStem(index, property, stem, stemTf, new String []
        { stem }, new double []
        { stemTf });
    }

}