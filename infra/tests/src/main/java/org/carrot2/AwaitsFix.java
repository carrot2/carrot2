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

/**
 * An annotation for known issues that await resolution (ignored by default to allow builds to
 * pass).
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@TestGroup(enabled = false)
public @interface AwaitsFix {
  /** Should point at the bug tracker issue (URL). */
  String value();
}
