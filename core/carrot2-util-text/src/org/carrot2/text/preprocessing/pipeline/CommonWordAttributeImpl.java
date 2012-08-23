package org.carrot2.text.preprocessing.pipeline;

import org.apache.lucene.util.AttributeImpl;

@SuppressWarnings("serial")
public class CommonWordAttributeImpl extends AttributeImpl implements CommonWordAttribute
{
    private boolean common; 

    @Override
    public boolean isCommon()
    {
        return common;
    }
    
    @Override
    public void setCommon(boolean v)
    {
        this.common = v;
    }

    @Override
    public void clear()
    {
        setCommon(false);
    }

    @Override
    public void copyTo(AttributeImpl other)
    {
        ((CommonWordAttributeImpl) other).setCommon(isCommon());
    }
}

