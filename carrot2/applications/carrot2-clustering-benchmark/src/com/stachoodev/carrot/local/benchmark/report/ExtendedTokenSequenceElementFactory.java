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
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;

/**
 * Converts
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.ExtendedTokenSequence}s to
 * XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExtendedTokenSequenceElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        ExtendedTokenSequence extendedTokenSequence = (ExtendedTokenSequence) object;
        Element tokenSequenceElement = DocumentHelper
            .createElement("token-sequence");

        // Try to get the most frequent _real_ phrase, which should be more
        // human-friendly
        List originalSequences = (List) extendedTokenSequence
            .getProperty(ExtendedTokenSequence.PROPERTY_ORIGINAL_TOKEN_SEQUENCES);
        if (originalSequences != null && originalSequences.size() > 0)
        {
            tokenSequenceElement.addElement("image").addText(
                originalSequences.get(0).toString());
        }
        else
        {
            tokenSequenceElement.addElement("image").addText(
                extendedTokenSequence.toString());
        }

        tokenSequenceElement.addElement("tf").addText(
            StringUtils.toString(new Double(extendedTokenSequence
                .getDoubleProperty(ExtendedToken.PROPERTY_TF, -1)), "#.##"));

        return tokenSequenceElement;
    }
}