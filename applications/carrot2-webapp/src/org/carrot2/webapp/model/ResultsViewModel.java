
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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * Represents search results view
 */
public class ResultsViewModel extends ModelWithDefault
{
    @Attribute
    public String id;

    @Element
    public String label;
}
