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
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
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