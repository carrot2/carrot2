
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

import org.carrot2.core.IClusteringAlgorithm;
import org.eclipse.core.runtime.IPath;

/**
 * Save/load attribute values of the current {@link SearchInput}'s
 * {@link IClusteringAlgorithm}.
 */
final class SaveAlgorithmAttributesAction extends SaveAttributesAction
{
    private final SearchInput searchInput;

    /*
     * 
     */
    public SaveAlgorithmAttributesAction(SearchInput searchInput)
    {
        super("Manage attributes");
        this.searchInput = searchInput;
    }

    /*
     * 
     */
    @Override
    protected void applyAttributes(Map<String, Object> attrs)
    {
        for (Map.Entry<String, Object> e : attrs.entrySet())
        {
            searchInput.setAttribute(e.getKey(), e.getValue());
        }
    }

    /*
     * 
     */
    @Override
    protected Map<String, Object> collectAttributes()
    {
        return searchInput.getAttributeValueSet().getAttributeValues();
    }

    /*
     * 
     */
    @Override
    protected String getComponentId()
    {
        return searchInput.getAlgorithmId();
    }

    @Override
    protected IPath getFileNameHint()
    {
        return getDefaultHint(searchInput.getAlgorithmId(), "algorithm-");
    }
}
