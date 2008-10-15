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
    }
}
