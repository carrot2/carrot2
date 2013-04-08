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
