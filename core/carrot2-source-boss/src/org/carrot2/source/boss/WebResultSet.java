
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.boss;

import java.util.List;

import org.simpleframework.xml.*;

@Root(name = "resultset_web", strict = false)
class WebResultSet
{
    @Attribute(required = false)
    public Long count;

    @Attribute(required = false)
    public Long start;

    @Attribute(required = false)
    public Long totalhits;

    @Attribute(required = false)
    public Long deephits;
    
    @ElementList(inline = true, entry = "result", required = false)
    public List<WebResult> results;
}
