
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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A single Web search result.
 */
@Root(name = "result", strict = false)
final class WebResult
{
    @Element(name = "abstract", required = false)
    public String summary;

    @Element(required = false)
    public String date;
    
    @Element(name = "dispurl", required = false)
    public String displayURL;
    
    @Element(name = "clickurl", required = false)
    public String clickURL;

    /**
     * Image size. The size can be given in textual format, e.g., <code>2MB</code>.
     */
    @Element(required = false)
    public String size;

    @Element(required = false)
    public String title;

    @Element(required = false)
    public String url;
}
