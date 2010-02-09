
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.lingo;

import org.carrot2.text.vsm.TermDocumentMatrixBuilderTestBase;

/**
 * Test cases for cluster merging in {@link ClusterBuilder}.
 */
public class LingoProcessingComponentTestBase extends TermDocumentMatrixBuilderTestBase
{
    protected LingoProcessingContext lingoContext;

    @Override
    protected void buildTermDocumentMatrix()
    {
        super.buildTermDocumentMatrix();
        lingoContext = new LingoProcessingContext(vsmContext);
    }
}
