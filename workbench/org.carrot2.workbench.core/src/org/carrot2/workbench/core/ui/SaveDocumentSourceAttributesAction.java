
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

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.core.IDocumentSource;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
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
    protected AttributeValueSets collectAttributes()
    {
        /*
         * Extract default attributes.
         */
        final String sourceId = searchInputView.getSourceId();
        assert sourceId != null;

        final AttributeValueSet defaults = getDefaultAttributeValueSet(sourceId);

        /*
         * Create an AVS for the default values and a based-on AVS with overridden values.
         */
        final AttributeValueSet overridenAvs = new AttributeValueSet(
            "overridden-attributes", defaults);
        final Map<String, Object> overrides = searchInputView
            .filterAttributesOf(sourceId);
        removeSpecialKeys(overrides);
        removeKeysWithDefaultValues(overrides, defaults);
        overridenAvs.setAttributeValues(overrides);

        // Flatten and save.
        final AttributeValueSets merged = new AttributeValueSets();
        merged.addAttributeValueSet(overridenAvs.label, overridenAvs);
        merged.addAttributeValueSet(defaults.label, defaults);
        merged.setDefaultAttributeValueSetId(overridenAvs.label);
        return merged;
    }

    /*
     * 
     */
    @Override
    protected void applyAttributes(AttributeValueSets attrs)
    {
        for (Map.Entry<String, Object> e : attrs.getDefaultAttributeValueSet()
            .getAttributeValues().entrySet())
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
