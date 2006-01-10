
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.mwroblewski.carrot.lexical;


import org.dom4j.Element;


/**
 * @author Michał Wróblewski
 */
public abstract class LexicalElement
{
    public abstract String toString();


    public abstract Element toXML();


    public static LexicalElement fromXML(Element element)
    {
        String type = element.attributeValue("type");

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
