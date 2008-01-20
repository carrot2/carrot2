
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.measure;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.Clusterable;

/**
 * Interface for similarity measure.
 */
public interface Similarity {
    public double measure(Clusterable obj1, Clusterable obj2);
    public double measure(double[] vector1, double[] vector2);
}
