
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import java.util.List;

/**
 * Descriptor of a {@link IDocumentSource} being part of a {@link ProcessingComponentSuite}.
 */
public class DocumentSourceDescriptor extends ProcessingComponentDescriptor
{
    private List<String> exampleQueries;

    DocumentSourceDescriptor()
    {
    }

    public List<String> getExampleQueries()
    {
        return exampleQueries;
    }
}
