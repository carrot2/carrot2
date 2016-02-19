
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

package org.carrot2.output.metrics;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

/**
 * A clustering quality metric. Calculates some metric reflecting the quality of
 * clustering. This interface does not impose any specific constraints on the values of
 * the metric, such as type and range. Instead, specific implementations should annotate
 * the values to be returned with {@link Output} {@link Processing} {@link Attribute}.
 */
public interface IClusteringMetric
{
    /**
     * Triggers calculation of the metric. All {@link Processing} {@link Input} attributes
     * will have been bound before a call to this method.
     */
    public void calculate();

    /**
     * Return <code>true</code> if this metric should be calculated.
     */
    public boolean isEnabled();
}
