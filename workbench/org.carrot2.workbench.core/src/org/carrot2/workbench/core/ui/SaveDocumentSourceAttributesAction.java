
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

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.core.IDocumentSource;
import org.eclipse.core.runtime.IPath;

/**
 * Save/load attribute values of the current {@link IDocumentSource}.
 */
final class SaveDocumentSourceAttributesAction extends SaveAttributesAction
{
    /**
     * The search input view we will collect attributes from or populate with loaded
     * attributes.
     */
    private SearchInputView searchInputView;

    /*
     * 
     */
    public SaveDocumentSourceAttributesAction(SearchInputView searchInputView)
    {
        super("Manage attributes");
        this.searchInputView = searchInputView;
    }

    /*
     * 
     */
    @Override
    protected Map<String, Object> collectAttributes()
    {
        return searchInputView.filterAttributesOf(getComponentId());
    }
    
    /*
     * 
     */
    @Override
    protected String getComponentId()
    {
        return searchInputView.getSourceId();
    }

    /*
     * 
     */
    @Override
    protected void applyAttributes(Map<String, Object> attrs)
    {
        for (Map.Entry<String, Object> e : attrs.entrySet())
        {
            this.searchInputView.setAttribute(e.getKey(), e.getValue());
        }
    }

    /*
     * 
     */
    @Override
    protected IPath getFileNameHint()
    {
        return getDefaultHint(this.searchInputView.getSourceId(), "source-");
    }
}
