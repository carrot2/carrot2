

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


import org.jdom.Element;


/**
 * @author Micha� Wr�blewski
 */
public abstract class LexicalElement
{
    public abstract String toString();


    public abstract Element toXML();


    public static LexicalElement fromXML(Element element)
    {
        String type = element.getAttributeValue("type");

        if (type.equals(Phrase.TYPE))
        {
            return new Phrase(element);
        }
        else if (type.equals(Term.TYPE))
        {
            return new Term(element);
        }
        else
        {
            return null;
        }
    }


    public boolean equals(Object o)
        throws ClassCastException
    {
        LexicalElement l = (LexicalElement) o;

        return toString().equals(l.toString());
    }
}
