package org.carrot2.text.preprocessing.pipeline;

import org.apache.lucene.util.AttributeImpl;

@SuppressWarnings("serial")
public class TokenTypeAttributeImpl extends AttributeImpl implements TokenTypeAttribute
{
    private int type; 

    @Override
    public void setType(int type)
    {
        this.type = type;
    }
    
    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public void clear()
    {
        setType(0);
    }

    @Override
    public void copyTo(AttributeImpl other)
    {
        ((TokenTypeAttributeImpl) other).setType(getType());
    }
}

