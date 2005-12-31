

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


package com.mwroblewski.carrot.lexical;


import org.dom4j.DocumentFactory;
import org.dom4j.Element;


/**
 * @author Michał Wróblewski
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
        stem = element.attributeValue("stem");
    }

    public String toString()
    {
        return stem;
    }


    public Element toXML()
    {
        final DocumentFactory factory = new DocumentFactory();
        Element term = factory.createElement("term");
        term.addAttribute("type", TYPE);
        term.addAttribute("stem", stem);

        return term;
    }
}
