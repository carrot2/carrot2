
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

package org.carrot2.workbench.vis;

import java.util.List;

import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * A mirror of {@link ProcessingResult} used to serialize less data
 * for visualizations.
 */
@Root(name = "searchresult", strict = false)
public final class ProcessingResultMirror
{
    @Element(required = false)
    public String query;

    @ElementList(inline = true, required = false)
    public List<Document> documents;

    @ElementList(inline = true, required = false)
    public List<Cluster> clusters;
}