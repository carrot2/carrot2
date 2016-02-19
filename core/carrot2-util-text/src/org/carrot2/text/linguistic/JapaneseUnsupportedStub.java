
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.factory.IFactory;

/**
 * A stub signalling no support for Japanese.
 */
final class JapaneseUnsupportedStub implements IFactory<ITokenizer>
{
    @Override
    public ITokenizer createInstance()
    {
        throw new UnsupportedOperationException("No support for Japanese clustering (jira CARROT-903).");
    }
}
