
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
package com.dawidweiss.carrot.util;

import java.util.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RequestParametersUtils
{
    /**
     * Copies contents of the <code>sourceParameters</code> map to the
     * <code>destinationParameters</code> map. If in the source map a value is
     * a one-element {@link java.util.List}, in the destination map, the
     * corresponding key will be mapped to the object contained in the
     * one-element list.
     * 
     * @param sourceParameters
     * @param destinationParameters
     */
    public static void unwrapOneElementLists(Map sourceParameters,
        Map destinationParameters)
    {
        for (Iterator iter = sourceParameters.keySet().iterator(); iter.hasNext();)
        {
            Object key = iter.next();
            Object value = sourceParameters.get(key);
            
            if (value instanceof List)
            {
                List list = (List) value;
                if (list.size() == 1)
                {
                    value = list.get(0);
                }
            }
            
            destinationParameters.put(key, value);
        }
    }
}
