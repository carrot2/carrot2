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

import org.dom4j.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;

/**
 * Converts
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.ExtendedToken}s to
 * XML elements.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ExtendedTokenElementFactory implements ElementFactory
{
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.local.benchmark.report.ElementFactory#createElement(java.lang.Object)
     */
    public Element createElement(Object object)
    {
        ExtendedToken extendedToken = (ExtendedToken) object;
        Element tokenElement = DocumentHelper.createElement("token");

        tokenElement.addElement("image").addText(extendedToken.toString());

        tokenElement.addElement("tf").addText(
            StringUtils.toString(new Double(extendedToken.getDoubleProperty(
                ExtendedToken.PROPERTY_TF, -1)), "#.##"));
        tokenElement.addElement("idf").addText(
            StringUtils.toString(new Double(extendedToken.getDoubleProperty(
                ExtendedToken.PROPERTY_IDF, -1)), "#.##"));
        tokenElement.addElement("df").addText(
            StringUtils.toString(new Double(extendedToken.getDoubleProperty(
                ExtendedToken.PROPERTY_DF, -1)), "#.##"));

        Token token = extendedToken.getToken();
        if (token instanceof TypedToken)
        {
            if ((((TypedToken) token).getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
            {
                tokenElement.addAttribute("sw", "true");
            }
        }

        return tokenElement;
    }
}