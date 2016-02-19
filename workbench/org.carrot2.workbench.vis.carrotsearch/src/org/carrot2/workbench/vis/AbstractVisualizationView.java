
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

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.ui.IWorkbenchPart;

public abstract class AbstractVisualizationView extends PageBookViewBase
{
    protected final PageRec doCreatePage(IWorkbenchPart part) {
        AbstractBrowserVisualizationViewPage page = wrappedCreatePage(part);
        return new PageRec(part, page);
    }

    protected abstract AbstractBrowserVisualizationViewPage wrappedCreatePage(IWorkbenchPart part);
    
    @Override
    protected final void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        super.doDestroyPage(part, pageRecord);
    }

    /**
     * Only react to {@link SearchEditor} instances.
     */
    @Override
    protected boolean isImportant(IWorkbenchPart part)
    {
        return (part instanceof SearchEditor);
    }    
}