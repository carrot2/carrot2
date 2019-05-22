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
