package org.carrot2.text.linguistic;

import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;

public class ExtendedWhitespaceTokenizerFactory implements ITokenizerFactory
{
    @Override
    public ITokenizer createInstance()
    {
        return new ExtendedWhitespaceTokenizer();
    }
}
