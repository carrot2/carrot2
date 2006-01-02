
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.input.snippetreader.util;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import gnu.regexp.REMatchEnumeration;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.dom4j.Element;


/**
 * An "extended" regular expression, which provides some more functionality.
 */
public class ExtendedRegExp {
    protected static final String RE_MATCH = "match";
    protected static final String RE_REPLACE = "replace";
    protected static final String RE_REPLACE_REGEXP = "regexp";
    protected static final String RE_REPLACE_WITH = "with";
    private RE match;
    private List replacements;

    /**
     * Creates a new ExtendedRegExp object.
     */
    public ExtendedRegExp(Element reDescription) throws REException {
        match = new RE(reDescription.element(RE_MATCH).getText());
        replacements = new LinkedList();

        List rt = reDescription.elements(RE_REPLACE);

        for (ListIterator li = rt.listIterator(); li.hasNext();) {
            Element repentry = (Element) li.next();
            replacements.add(new RE(repentry.attributeValue(RE_REPLACE_REGEXP)));
            replacements.add(repentry.attributeValue(RE_REPLACE_WITH));
        }
    }

    public REMatch getMatch(Object onObject) {
        return match.getMatch(onObject);
    }

    public REMatchEnumeration getMatchEnumeration(Object onObject) {
        return match.getMatchEnumeration(onObject);
    }

    public String process(REMatch match) {
        String out = match.toString();
        ListIterator li = replacements.listIterator();

        while (li.hasNext()) {
            RE rg = (RE) li.next();
            String replacement = (String) li.next();
            out = rg.substituteAll(out, replacement);
        }

        return out;
    }

    public String getProcessedMatch(Object ob) {
        REMatch m = getMatch(ob);

        if (m == null) {
            return null;
        } else {
            return process(m);
        }
    }
}
