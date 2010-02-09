
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
 * A single image search result.
 */
@Root(name = "result", strict = false)
final class ImageResult
{
    /**
     * Description of the image.
     */
    @Element(name = "abstract", required = false)
    public String summary;

    /**
     * Title of the image (usually file name).
     */
    @Element(required = false)
    public String title;

    /**
     * URL to click to view the image.
     */
    @Element(name = "clickurl", required = false)
    public String clickURL;

    /**
     * Image size. The size can be given in textual format, e.g., <code>2MB</code>.
     */
    @Element(required = false)
    public String size;

    /**
     * File format.
     */
    @Element(required = false)
    public String format;

    @Element(required = false)
    public Long width;

    @Element(required = false)
    public Long height;

    @Element(required = false)
    public String date;

    @Element(required = false)
    public String mimetype;

    /**
     * Clickable URL of the image's origin page.
     */
    @Element(name = "refererclickurl", required = false)
    public String refererClickURL;

    /**
     * URL of the image's origin page.
     */
    @Element(name = "refererurl", required = false)
    public String refererURL;

    /**
     * Original image URL.
     */
    @Element(required = false)
    public String url;

    @Element(name = "thumbnail_width", required = false)
    public Long thumbnailWidth;

    @Element(name = "thumbnail_height", required = false)
    public Long thumbnailHeight;

    @Element(name = "thumbnail_url", required = false)
    public String thumbnailURL;    
}
