package org.carrot2.text.preprocessing.pipeline.lucene;

/**
 * An attribute for propagating token types.
 */
public interface TokenTypeAttribute extends org.apache.lucene.util.Attribute
{
    public int getType();
    public void setType(int type);
}
