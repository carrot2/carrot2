

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.lexical;


import org.jdom.Element;


/**
 * @author Micha� Wr�blewski
 */
public class Term
    extends LexicalElement
{
    protected static final String TYPE = "word";
    protected String stem;

    public Term(String stem)
    {
        this.stem = stem;
    }


    protected Term(Element element)
    {
        stem = element.getAttributeValue("stem");
    }

    public String toString()
    {
        return stem;
    }


    public Element toXML()
    {
        Element term = new Element("term");
        term.setAttribute("type", TYPE);
        term.setAttribute("stem", stem);

        return term;
    }
}
