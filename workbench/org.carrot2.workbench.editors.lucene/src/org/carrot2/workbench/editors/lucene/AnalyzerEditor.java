
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2013, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.editors.lucene;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.carrot2.util.StringUtils;
import org.carrot2.workbench.editors.AttributeEditorInfo;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.carrot2.workbench.editors.impl.MappedValueComboEditor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

/**
 * An {@link IAttributeEditor} for selecting Apache Lucene's {@link Analyzer} attribute.
 */
@SuppressWarnings("deprecation")
public class AnalyzerEditor extends MappedValueComboEditor
{
    Analyzer [] analyzers = new Analyzer [] {
        new StandardAnalyzer(Version.LUCENE_CURRENT),
        new WhitespaceAnalyzer(Version.LUCENE_CURRENT),
        new SimpleAnalyzer(Version.LUCENE_CURRENT)
    };

    /*
     * 
     */
    @Override
    protected AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        valueRequired = true;
        anyValueAllowed = false;

        BiMap<Object, String> valueToName = HashBiMap.create();
        ArrayList<Object> valueOrder = Lists.newArrayList();

        for (Analyzer analyzer : analyzers)
        {
            valueToName.put(
                analyzer, 
                StringUtils.splitCamelCase(ClassUtils.getShortClassName(analyzer.getClass())));
            valueOrder.add(analyzer);
        }
        super.setMappedValues(valueToName, valueOrder);

        return new AttributeEditorInfo(1, false);
    }
    
    @Override
    public void setValue(Object newValue)
    {
        /*
         * This is a terrible hack but there seems to be no easy way around it. We map
         * the *class* of an incoming value to any of the existing presets, since we don't
         * know what the default value in LuceneDocumentSource is. This should work fine for 
         * Workbench.
         */
        if (newValue instanceof Analyzer) {
            for (Analyzer preset : analyzers)
            {
                if (newValue.getClass().equals(preset.getClass()))
                {
                    super.setValue(preset);
                    return;
                }
            }
        }
        super.setValue(newValue);
    }
}
