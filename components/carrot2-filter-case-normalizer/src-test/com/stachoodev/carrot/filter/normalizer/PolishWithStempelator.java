/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.normalizer;

import com.dawidweiss.carrot.core.local.linguistic.Stemmer;
import com.dawidweiss.carrot.filter.stempelator.Stempelator;
import com.dawidweiss.carrot.util.tokenizer.languages.polish.Polish;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class PolishWithStempelator extends Polish
{
    protected Stemmer createStemmerInstance()
    {
        return new Stempelator();
    }
}
