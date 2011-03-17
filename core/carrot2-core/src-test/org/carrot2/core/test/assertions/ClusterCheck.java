package org.carrot2.core.test.assertions;

import org.carrot2.core.Cluster;

/**
 * Performs checks on an individual cluster. Very often using checks rather than FEST
 * Conditions results in better reporting because checks make their own asserts as opposed
 * to returning a single boolean value.
 */
public interface ClusterCheck
{
    public void check(Cluster cluster);
}
