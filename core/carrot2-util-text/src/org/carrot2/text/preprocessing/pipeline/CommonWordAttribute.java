package org.carrot2.text.preprocessing.pipeline;

/**
 * An attribute for propagating information about stop list terms.
 */
public interface CommonWordAttribute extends org.apache.lucene.util.Attribute
{
    public boolean isCommon();
    public void setCommon(boolean v);
}
