
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

package com.dawidweiss.carrot.input.snippetreader.extractors.regexp;

import org.jdom.Element;


/**
 * Regular expression token holder. Holds the regular expression string and an
 * indicator whether the match should be consumed, or included in the result.
 */
class RegularExpression {
    /**
     * Field XML_ATTR_REGEXP
     */
    public static final String XML_ATTR_REGEXP = "regexp";

    /**
     * Field XML_ATTR_CONSUME
     */
    public static final String XML_ATTR_CONSUME = "consume";

    protected String regExp;

    protected boolean consumeToken;

    /**
     * Constructor RegularExpression
     *
     * @param regExp
     * @param consume
     */
    public RegularExpression(String regExp, boolean consume) {
        this.regExp = regExp;
        this.consumeToken = consume;
    }

    /**
     * Constructor RegularExpression
     *
     * @param regExpXmlElement
     */
    public RegularExpression(Element regExpXmlElement) {
        if (regExpXmlElement == null) {
            throw new IllegalArgumentException(
                "Regular Expression cannot be instantiated from null Element.");
        }

        if (regExpXmlElement.getAttribute(XML_ATTR_REGEXP) != null) {
            regExp = regExpXmlElement.getAttribute(XML_ATTR_REGEXP).getValue();
        }

        if (regExpXmlElement.getChild(XML_ATTR_REGEXP) != null) {
            regExp = regExpXmlElement.getChild(XML_ATTR_REGEXP).getText();
        }

        if (regExp == null) {
            throw new IllegalArgumentException(
                "XML Element does not contain required attributes.");
        }

        if (regExpXmlElement.getAttribute(XML_ATTR_CONSUME) != null) {
            if ("true".equalsIgnoreCase(regExpXmlElement.getAttribute(
                            XML_ATTR_CONSUME).getValue())) {
                consumeToken = true;
            }
        }
    }

    /**
     * Method getRegExp
     *
     * @return
     */
    public String getRegExp() {
        return regExp;
    }

    /**
     * Method isConsumeToken
     *
     * @return
     */
    public boolean isConsumeToken() {
        return consumeToken;
    }
}
