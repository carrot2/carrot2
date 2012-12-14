package org.carrot2.text.preprocessing.pipeline.lucene;

import org.carrot2.text.analysis.ITokenizer;

/**
 * An attribute for providing token types from {@link ITokenizer}.
 */
public interface TokenTypeAttribute extends org.apache.lucene.util.Attribute
{
    public int getType();
    public void setType(int type);
}
