
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

package org.carrot2.workbench.vis.foamtree;

import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.vis.FlashViewPage;


/**
 * A single {@link FoamTreeView} page embedding a Web browser and redirecting to an
 * internal HTTP server with flash animation.
 */
final class FoamTreeViewPage extends FlashViewPage
{
    /**
     * Entry page for the view.
     */
    private static final String ENTRY_PAGE = "/foamtree/index.vm";

    /*
     * 
     */
    public FoamTreeViewPage(SearchEditor editor)
    {
        super(editor, ENTRY_PAGE);
    }
}
