
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis;

import java.util.List;

import org.carrot2.workbench.core.ui.PageBookViewBase;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class AbstractVisualizationView extends PageBookViewBase
{
    private List<AbstractBrowserVisualizationViewPage> listeners = Lists.newArrayList();
    
    @Override
    public void createPartControl(Composite parent)
    {
        super.createPartControl(parent);

        getPageBook().addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                if (!getPageBook().isVisible()) return;

                Rectangle clientArea = getPageBook().getClientArea();
                LoggerFactory.getLogger("Foo").warn("controlResized(): " + 
                  clientArea + " => " + listeners.size());
                for (AbstractBrowserVisualizationViewPage page : listeners) {
                    page.updateSize(clientArea);
                }
            }
        });
    }
    
    protected final PageRec doCreatePage(IWorkbenchPart part) {
        AbstractBrowserVisualizationViewPage page = wrappedCreatePage(part);
        listeners.add(page);
        return new PageRec(part, page);
    }

    protected abstract AbstractBrowserVisualizationViewPage wrappedCreatePage(IWorkbenchPart part);
    
    @Override
    protected final void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
    {
        listeners.remove(pageRecord.page);
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