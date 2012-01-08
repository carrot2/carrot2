
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.constraint;

/**
 * A single resource filter (pattern and description).
 */
public @interface ResourceNameFilter
{
    /** Resource pattern. Example: <code>*.xml;*.XML</code> */
    String pattern();

    /** Description of the pattern. Example: "XML files". */
    String description();
}
