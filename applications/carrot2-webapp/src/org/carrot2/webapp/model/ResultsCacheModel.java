
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.model;

import java.util.List;

import org.carrot2.core.IProcessingComponent;
import org.simpleframework.xml.Attribute;

/**
 * Represents results caching configuration entry
 */
public class ResultsCacheModel
{
    @Attribute
    public Class<? extends IProcessingComponent> component;
    
    @SuppressWarnings("unchecked")
    public static Class<? extends IProcessingComponent> [] toClassArray(List<ResultsCacheModel> list)
    {
        final Class<? extends IProcessingComponent> [] result = new Class[list.size()];
        int i = 0;
        for (ResultsCacheModel resultsCacheModel : list)
        {
            result[i++] = resultsCacheModel.component;
        } 
        return result;
    }
}
