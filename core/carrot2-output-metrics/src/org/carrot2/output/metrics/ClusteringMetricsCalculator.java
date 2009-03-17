/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.output.metrics;

import org.carrot2.core.ProcessingComponentBase;
import org.carrot2.core.ProcessingException;
import org.carrot2.util.attribute.Bindable;

/**
 * Calculates a set of quality metrics for clusters.
 */
@Bindable
public class ClusteringMetricsCalculator extends ProcessingComponentBase
{
    /**
     * Contamination metric.
     */
    public ContaminationMetric contamination = new ContaminationMetric();

    /**
     * Coverage metrics.
     */
    public CoverageMetric coverage = new CoverageMetric();

    /**
     * Precision and recall based metrics.
     */
    public PrecisionRecallMetric precisionRecall = new PrecisionRecallMetric();

    @Override
    public void process() throws ProcessingException
    {
        if (contamination.isEnabled())
        {
            contamination.calculate();
        }

        if (coverage.isEnabled())
        {
            coverage.calculate();
        }

        if (precisionRecall.isEnabled())
        {
            precisionRecall.calculate();
        }
    }
}
