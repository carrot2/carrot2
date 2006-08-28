
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.normalizer;


/**
 * Extends {@link CaseNormalizerLocalFilterComponent} using
 * {@link SimpleCaseNormalizer}.
 * 
 * @author Dawid Weiss
 */
public final class SimpleCaseNormalizerLocalFilterComponent 
    extends CaseNormalizerLocalFilterComponent
{
    public SimpleCaseNormalizerLocalFilterComponent() {
        super(new SimpleCaseNormalizer());
    }
}
