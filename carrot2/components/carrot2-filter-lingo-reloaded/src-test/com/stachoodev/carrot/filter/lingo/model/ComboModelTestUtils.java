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

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ComboModelTestUtils
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
    static public ExtendedToken createTokenStem(String stem, double stemTf,
        double stemDf, String originalTokenImage)
    {
        StringTypedToken token = new StringTypedToken();
        token.assign(stem, (short) 0);
        ExtendedToken extendedTokenStem = new ExtendedToken(
            new TokenStem(token));
        extendedTokenStem.setDoubleProperty(ExtendedToken.PROPERTY_TF, stemTf);
        extendedTokenStem.setDoubleProperty(ExtendedToken.PROPERTY_DF, stemDf);

        StringTypedToken originalToken = new StringTypedToken();
        originalToken.assign(originalTokenImage, (short) 0);
        extendedTokenStem.setProperty(
            ExtendedToken.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN, originalToken);

        return extendedTokenStem;
    }

    /**
     * @param index
     * @param property
     * @param stem
     * @param stemTf
     * @return
     */
    static public ExtendedToken createTokenStem(String stem, double stemTf,
        double stemDf)
    {
        return createTokenStem(stem, stemTf, stemDf, stem);
    }

    /**
     * @param index
     * @param property
     * @param stem
     * @param stemTf
     * @param originalTokens
     * @param originalTf
     * @return
     */
    static public ExtendedToken createTokenStem(String property, String stem,
        double stemTf, String originalTokenImage)
    {
        StringTypedToken token = new StringTypedToken();
        token.assign(stem, (short) 0);
        ExtendedToken extendedTokenStem = new ExtendedToken(
            new TokenStem(token));
        extendedTokenStem.setDoubleProperty(property, stemTf);

        StringTypedToken originalToken = new StringTypedToken();
        originalToken.assign(originalTokenImage, (short) 0);
        extendedTokenStem.setProperty(
            ExtendedToken.PROPERTY_MOST_FREQUENT_ORIGINAL_TOKEN, originalToken);

        return extendedTokenStem;
    }

    /**
     * @param index
     * @param property
     * @param stem
     * @param stemTf
     * @return
     */
    static public ExtendedToken createTokenStem(String property, String stem,
        double stemTf)
    {
        return createTokenStem(property, stem, stemTf, stem);
    }
}