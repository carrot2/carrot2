package org.carrot2.text.linguistic;

import org.carrot2.text.analysis.ITokenizer;

public interface ITokenizerFactory
{
    public ITokenizer createInstance();
}
