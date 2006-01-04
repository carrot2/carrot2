
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
package com.dawidweiss.carrot.input.snippetreader.extractors.regexp;

import org.dom4j.Element;


/**
 * Regular expression token holder. Holds the regular expression string and an
 * indicator whether the match should be consumed, or included in the result.
 */
class RegularExpression {
    public static final String XML_ATTR_REGEXP = "regexp";
    public static final String XML_ATTR_CONSUME = "consume";

    private String regExp;
    private boolean consumeToken;

    public RegularExpression(String regExp, boolean consume) {
        this.regExp = regExp;
        this.consumeToken = consume;
    }

    public RegularExpression(Element regExpXmlElement) {
        if (regExpXmlElement == null) {
            throw new IllegalArgumentException(
                "Regular Expression cannot be instantiated from null Element.");
        }

        if (regExpXmlElement.attribute(XML_ATTR_REGEXP) != null) {
            regExp = regExpXmlElement.attributeValue(XML_ATTR_REGEXP);
        }

        if (regExpXmlElement.element(XML_ATTR_REGEXP) != null) {
            regExp = regExpXmlElement.elementText(XML_ATTR_REGEXP);
        }

        if (regExp == null) {
            throw new IllegalArgumentException(
                "XML Element does not contain required attributes.");
        }

        if (regExpXmlElement.attribute(XML_ATTR_CONSUME) != null) {
            if ("true".equalsIgnoreCase(regExpXmlElement.attributeValue(XML_ATTR_CONSUME))) {
                consumeToken = true;
            }
        }
    }

    public String getRegExp() {
        return regExp;
    }

    public boolean isConsumeToken() {
        return consumeToken;
    }
}
