

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.termsfilter;


import com.mwroblewski.carrot.filter.termsfilter.weighing.TermsWeighing;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author Micha� Wr�blewski
 */
public class TermsFilterParams
{
    protected Map params;

    public TermsFilterParams(Map params)
    {
        this.params = params;
    }

    protected String getParam(String paramName)
    {
        LinkedList param = (LinkedList) params.get(paramName);

        return (String) param.get(0);
    }


    public TermsWeighing getTermsWeighing()
        throws Exception
    {
        Class termsWeighingClass = Class.forName(getParam("termsWeighing"));

        return (TermsWeighing) termsWeighingClass.newInstance();
    }


    public int getMaxPhrasesLength()
    {
        return Integer.parseInt(getParam("maxPhrasesLength"));
    }


    public float getMinPhrasesStrength()
    {
        return Float.parseFloat(getParam("minPhrasesStrength"));
    }


    public float getStrongTermsWeight()
    {
        return Float.parseFloat(getParam("strongTermsWeight"));
    }


    public boolean getRemoveQuery()
        throws Exception
    {
        return Boolean.valueOf(getParam("removeQuery")).booleanValue();
    }


    public boolean getRemoveSingleTerms()
        throws Exception
    {
        return Boolean.valueOf(getParam("removeSingleTerms")).booleanValue();
    }
}
