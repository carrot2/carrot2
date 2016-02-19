
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
     * Precision and recall based metrics.
     */
    public PrecisionRecallMetric precisionRecall = new PrecisionRecallMetric();

    /**
     * Normalized Mutual Information metric.
     */
    public NormalizedMutualInformationMetric normalizedMutualInformation = new NormalizedMutualInformationMetric();

    @Override
    public void process() throws ProcessingException
    {
        if (contamination.isEnabled())
        {
            contamination.calculate();
        }

        if (precisionRecall.isEnabled())
        {
            precisionRecall.calculate();
        }
        
        if (normalizedMutualInformation.isEnabled())
        {
            normalizedMutualInformation.calculate();
        }
    }
}
