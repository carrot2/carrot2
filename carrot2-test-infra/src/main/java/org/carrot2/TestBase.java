
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2;

import com.carrotsearch.randomizedtesting.MixWithSuiteName;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.annotations.*;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakAction.Action;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakGroup.Group;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakZombies.Consequence;
import com.carrotsearch.randomizedtesting.rules.NoClassHooksShadowingRule;
import com.carrotsearch.randomizedtesting.rules.NoInstanceHooksOverridesRule;
import com.carrotsearch.randomizedtesting.rules.SystemPropertiesInvariantRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

@TimeoutSuite(millis = 180 * 1000) // No suite should run longer than 180 seconds.
@ThreadLeakGroup(Group.MAIN)
@ThreadLeakScope(Scope.TEST)
@ThreadLeakZombies(Consequence.IGNORE_REMAINING_TESTS)
@ThreadLeakLingering(linger = 1000)
@ThreadLeakAction({Action.WARN, Action.INTERRUPT})
@SeedDecorators({MixWithSuiteName.class})
public abstract class TestBase extends RandomizedTest {
  /**
   * These property keys will be ignored in verification of altered properties.
   *
   * @see SystemPropertiesInvariantRule
   * @see #classRules
   */
  private static final String[] IGNORED_INVARIANT_PROPERTIES = {
      "user.timezone",
      "jetty.git.hash"
  };

  /**
   * Class {@link TestRule}s.
   */
  @ClassRule
  public static final TestRule classRules;

  static {
    RuleChain rules = RuleChain.outerRule(new SystemPropertiesInvariantRule(IGNORED_INVARIANT_PROPERTIES));
    rules = rules.around(new NoClassHooksShadowingRule()).around(new NoInstanceHooksOverridesRule());
    classRules = rules;
  }

  /**
   * Test {@link TestRule}s.
   */
  @Rule
  public final TestRule ruleChain =
      RuleChain.outerRule(new SystemPropertiesInvariantRule(IGNORED_INVARIANT_PROPERTIES));

  /**
   * Test logger.
   */
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected byte[] resourceBytes(String resource) {
    try (InputStream is = getClass().getResourceAsStream(resource)) {
      if (is == null) {
        throw new RuntimeException("Resource not found: " + resource);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte [] buf = new byte [1024];
      for (int len; (len = is.read(buf)) > 0;) {
        baos.write(buf, 0, len);
      }
      return baos.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected String resourceString(String resource) {
    return new String(resourceBytes(resource), StandardCharsets.UTF_8);
  }

  protected InputStream resourceStream(String resource) {
    return new ByteArrayInputStream(resourceBytes(resource));
  }
}
