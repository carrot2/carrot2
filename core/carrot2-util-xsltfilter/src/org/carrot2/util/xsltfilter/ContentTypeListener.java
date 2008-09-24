/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.xsltfilter;

/**
 * A callback listener for providing information about the content type and encoding of
 * the output.
 * 
 * @author Dawid Weiss
 */
interface ContentTypeListener
{
    public void setContentType(String contentType, String encoding);
}
