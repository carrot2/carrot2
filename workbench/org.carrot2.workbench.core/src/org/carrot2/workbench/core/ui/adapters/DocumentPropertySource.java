
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.adapters;

import java.util.Map;

import org.carrot2.core.Document;

import com.google.common.collect.Maps;

/**
 * 
 */
public final class DocumentPropertySource extends MapPropertySource
{
    public DocumentPropertySource(Document document)
    {
        final Map<String, Object> properties = Maps.newHashMap();
        
        properties.put("ID", document.getId());
        properties.put("URL", document.getField(Document.CONTENT_URL));
        properties.put("summary", document.getField(Document.SUMMARY));
        properties.put("title", document.getField(Document.TITLE));
        properties.put("fields", document.getFields());

        add(properties, null);
    }
}
