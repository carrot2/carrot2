
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

import java.util.HashMap;
import java.util.Map;

@TimeoutSuite(millis = 180 * 1000) // No suite should run longer than 180 seconds.
@ThreadLeakGroup(Group.MAIN)
@ThreadLeakScope(Scope.TEST)
@ThreadLeakZombies(Consequence.IGNORE_REMAINING_TESTS)
@ThreadLeakLingering(linger = 1000)
@ThreadLeakAction({Action.WARN, Action.INTERRUPT})
@SeedDecorators({MixWithSuiteName.class})
public abstract class AbstractTest extends RandomizedTest
{
    /**
     * These property keys will be ignored in verification of altered properties.
     *
     * @see SystemPropertiesInvariantRule
     * @see #classRules
     */
    private static final String [] IGNORED_INVARIANT_PROPERTIES = {
      "user.timezone"
    };

    /**
     * Class {@link TestRule}s.
     */
    @ClassRule
    public static final TestRule classRules;
    static {
      RuleChain rules = RuleChain.outerRule(new SystemPropertiesInvariantRule(IGNORED_INVARIANT_PROPERTIES));
      rules = rules.around(new NoClassHooksShadowingRule())
                   .around(new NoInstanceHooksOverridesRule());
      classRules = rules;
    }

    /**
     * Test {@link TestRule}s.
     */
    @Rule
    public final TestRule ruleChain = RuleChain
      .outerRule(new SystemPropertiesInvariantRule(IGNORED_INVARIANT_PROPERTIES));

    public static <K, V> Map<K, V> mapOf(K k, V v) {
        HashMap<K, V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        HashMap<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
}
