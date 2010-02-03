
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

import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
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

    /**
     * 
     */
    @Override
    protected void applyAttributes(AttributeValueSets attrs)
    {
        for (Map.Entry<String, Object> e : attrs.getDefaultAttributeValueSet()
            .getAttributeValues().entrySet())
        {
            searchInput.setAttribute(e.getKey(), e.getValue());
        }        
    }

    /**
     * 
     */
    @Override
    protected AttributeValueSets collectAttributes()
    {
        /*
         * Extract all @Input defaults for a given algorithm.
         */
        final AttributeValueSet defaults = getDefaultAttributeValueSet(searchInput
            .getAlgorithmId());

        /*
         * Create an AVS for the default values and a based-on AVS with overridden
         * values.
         */
        final AttributeValueSet avs = searchInput.getAttributeValueSet();
        final Map<String, Object> overrides = avs.getAttributeValues();
        removeSpecialKeys(overrides);
        removeKeysWithDefaultValues(overrides, defaults);
        overrides.keySet().retainAll(defaults.getAttributeValues().keySet());

        final AttributeValueSet overridenAvs = new AttributeValueSet(
            "overridden-attributes", defaults);
        overridenAvs.setAttributeValues(overrides);

        // Flatten and save.
        final AttributeValueSets merged = new AttributeValueSets();
        merged.addAttributeValueSet(overridenAvs.label, overridenAvs);
        merged.addAttributeValueSet(defaults.label, defaults);
        merged.setDefaultAttributeValueSetId(overridenAvs.label);

        return merged;
    }
    
    @Override
    protected IPath getFileNameHint()
    {
        return getDefaultHint(searchInput.getAlgorithmId(), "algorithm-");
    }
}
