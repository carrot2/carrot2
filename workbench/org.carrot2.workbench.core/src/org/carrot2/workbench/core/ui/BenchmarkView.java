/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import org.eclipse.ui.IWorkbenchPart;

/**
 * {@link SearchEditor}-tied view for running benchmarks.
 */
public final class BenchmarkView extends PageBookViewBase
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.benchmark";

    /**
     * Create the benchmarking view for a given part.
     */
    @Override
    protected PageRec doCreatePage(IWorkbenchPart part)
    {
        final SearchEditor editor = (SearchEditor) part;

        final BenchmarkViewPage page = new BenchmarkViewPage(editor);
        initPage(page);
        page.createControl(getPageBook());

        return new PageRec(part, page);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return part instanceof SearchEditor;
    }
}
