
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

package org.carrot2.core;

import java.util.List;

import org.simpleframework.xml.ElementList;

/**
 * Descriptor of a {@link IDocumentSource} being part of a {@link ProcessingComponentSuite}.
 */
public class DocumentSourceDescriptor extends ProcessingComponentDescriptor
{
    @ElementList(name = "example-queries", entry = "example-query", required = false)
    private List<String> exampleQueries;

    DocumentSourceDescriptor()
    {
    }

    public List<String> getExampleQueries()
    {
        return exampleQueries;
    }
}
