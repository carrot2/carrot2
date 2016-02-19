
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

/**
 *
 */
public class SkinModel extends ModelWithDefault
{
    @Attribute
    public String id;

    public final RequestType resultsRequestType = RequestType.PAGE;

    @Attribute
    public boolean sprited;
}
