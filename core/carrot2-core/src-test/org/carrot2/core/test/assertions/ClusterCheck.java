
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
