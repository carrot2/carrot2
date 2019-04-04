package org.carrot2;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.carrotsearch.randomizedtesting.annotations.TestGroup;

/**
 * Indicates slow tests. May be disabled by default to speed up local execution
 * for developers.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@TestGroup(enabled = false)
public @interface Slow {
}
