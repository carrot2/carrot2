/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2;

import com.carrotsearch.randomizedtesting.annotations.TestGroup;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Indicates slow tests. May be disabled by default to speed up local execution for developers. */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@TestGroup(enabled = false)
public @interface Slow {}
