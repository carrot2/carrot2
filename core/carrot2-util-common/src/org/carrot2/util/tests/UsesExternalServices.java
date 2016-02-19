
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

package org.carrot2.util.tests;

import java.lang.annotation.*;

import com.carrotsearch.randomizedtesting.annotations.TestGroup;

/**
 * A test group that indicates a given test uses external APIs or services (some of which
 * may not be available outside of the scope of Carrot2 build servers).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@TestGroup(enabled = false, sysProperty="tests.externalservices")
public @interface UsesExternalServices
{
}
