
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.vis.circles;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.vis.FlashViewPage;


/**
 * A single {@link CirclesView} page embedding a Web browser and redirecting to an
 * internal HTTP server with flash animation.
 */
final class CirclesViewPage extends FlashViewPage
{
    /**
     * Entry page for the view.
     */
    private static final String ENTRY_PAGE = "/circles/index.vm";

    /*
     * 
     */
    public CirclesViewPage(SearchEditor editor)
    {
        super(editor, ENTRY_PAGE);
    }
}
