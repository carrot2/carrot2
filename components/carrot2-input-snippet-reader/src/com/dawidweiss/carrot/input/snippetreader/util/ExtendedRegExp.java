
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.input.snippetreader.util;

import org.jdom.Element;

import gnu.regexp.*;

import java.util.*;


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
    public ExtendedRegExp(Element jdomReDescription) throws REException {
        match = new RE(jdomReDescription.getChild(RE_MATCH).getText());
        replacements = new LinkedList();

        List rt = jdomReDescription.getChildren(RE_REPLACE);

        for (ListIterator li = rt.listIterator(); li.hasNext();) {
            Element repentry = (Element) li.next();
            replacements.add(new RE(repentry.getAttribute(RE_REPLACE_REGEXP)
                                            .getValue()));
            replacements.add(repentry.getAttribute(RE_REPLACE_WITH).getValue());
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
