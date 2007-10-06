/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.aggregator;

import org.carrot2.core.LocalComponentFactory;

/**
 * Describes one input component used by the {@link AggregatorInputComponent}.
 * 
 * @author Stanislaw Osinski
 */
public class AggregatorInput
{
    /**
     * Identifier of this input. Must be unique across all input components
     * passed to the aggegator.
     */
    public String inputId;

    /** Factory that produces instances of this input component. */
    public LocalComponentFactory inputFactory;

    /**
     * Weight of this input component. Proportionally to the weights of all the
     * aggregated inputs, the aggregator will compute the number of results
     * requested from each input.
     */
    public double weight;

    public AggregatorInput(String inputId, LocalComponentFactory inputFactory,
        double weight)
    {
        super();
        this.inputId = inputId;
        this.inputFactory = inputFactory;
        this.weight = weight;
    }
}
