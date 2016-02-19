
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

package org.carrot2.util.annotations;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * Marker interface for classes that can be used safely by more than one thread (concurrently).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface ThreadSafe
{
}
