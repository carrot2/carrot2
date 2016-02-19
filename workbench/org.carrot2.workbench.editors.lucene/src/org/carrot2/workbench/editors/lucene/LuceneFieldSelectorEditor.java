
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

package org.carrot2.workbench.editors.lucene;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.store.Directory;
import org.carrot2.source.lucene.LuceneDocumentSource;
import org.carrot2.util.attribute.AttributeUtils;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.impl.MappedValueComboEditor;

import org.carrot2.shaded.guava.common.collect.*;

/**
 * Editor for mapped values (enumerated types and unrestricted strings with enum hints).
 */
public final class LuceneFieldSelectorEditor extends MappedValueComboEditor
{
    private final static String dirKey = AttributeUtils.getKey(
        LuceneDocumentSource.class, "directory");

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(Map<String, Object> defaultValues)
    {
        valueRequired = false;
        anyValueAllowed = true;

        refreshFields(defaultValues.get(dirKey));

        /*
         * 
         */
        super.eventProvider.addAttributeListener(new AttributeListenerAdapter()
        {
            @Override
            public void valueChanged(AttributeEvent event)
            {
                if (StringUtils.equals(event.key, dirKey))
                {
                    refreshFields(event.value);
                }
            }
        });

        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    private void refreshFields(Object directory)
    {
        BiMap<Object, String> valueToName = HashBiMap.create();
        ArrayList<Object> valueOrder = Lists.newArrayList();

        if (directory != null && directory instanceof Directory)
        {
            try
            {
                final Directory dir = (Directory) directory;
                final IndexReader ir = DirectoryReader.open(dir);
                try
                {
                    List<String> all = Lists.newArrayList();
                    for (FieldInfo fi : MultiFields.getMergedFieldInfos(ir))
                    {
                        all.add(fi.name);
                    }

                    Collections.sort(all);
                    for (String field : all)
                    {
                        valueToName.put(field, field);
                        valueOrder.add(field);
                    }
                }
                finally
                {
                    ir.close();
                }
            }
            catch (Exception e)
            {
                Utils.logError("Index access error.", e, false);
            }
        }

        super.setMappedValues(valueToName, valueOrder);
    }

    @Override
    public void setValue(Object newValue)
    {
        super.setValue(newValue);
    }
}
