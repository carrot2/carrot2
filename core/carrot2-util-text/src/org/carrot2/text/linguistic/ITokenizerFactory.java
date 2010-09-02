package org.carrot2.text.linguistic;

import org.carrot2.text.analysis.ITokenizer;

interface ITokenizerFactory
{
    public ITokenizer createInstance();
}
