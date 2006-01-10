
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
