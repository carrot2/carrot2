
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

package org.carrot2.workbench.velocity;

import java.lang.annotation.Annotation;

/**
 * Utilities for annotation display.
 */
public final class AnnotationUtils
{
    public static String shortName(Annotation ann)
    {
        return ann.toString().replaceAll("@([a-zA-Z0-9_]+\\.)+", "@");
    }
}
