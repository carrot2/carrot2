package com.paulodev.carrot.treeSnippetMiner;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

public class Int
{
    private int value;

    public Int(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public void incValue()
    {
        value++;
    }

    public void incValue(int toAdd)
    {
        value += toAdd;
    }

    public void decValue()
    {
        value--;
    }

    public void decValue(int toDec)
    {
        value -= toDec;
    }

    public int hashCode()
    {
        return value;
    }
}