
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

import java.io.*;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * Factory storing and restoring {@link SearchInput} instances from {@link IMemento}.
 */
public final class SearchInputFactory implements IElementFactory
{
    private static final String ID_ATTRIBUTE = "id";
    private static final String ALGORITHM_ELEMENT = "algorithm";
    private static final String SOURCE_ELEMENT = "source";
    private static final String ATTRIBUTES_ELEMENT = "attributes";

    /**
     * Public identifier of this {@link IElementFactory}.
     */
    public static final String ID = "org.carrot2.workbench.core.searchParametersFactory";

    /*
     * 
     */
    public IAdaptable createElement(IMemento memento)
    {
        String source = tryGetStringFrom(memento, SOURCE_ELEMENT, ID_ATTRIBUTE);
        String algorithm = tryGetStringFrom(memento, ALGORITHM_ELEMENT, ID_ATTRIBUTE);
        if (StringUtils.isBlank(source) || StringUtils.isBlank(algorithm))
        {
            return null;
        }

        final SearchInput search;
        AttributeValueSet attributes = null;
        try
        {
            IMemento attMemento = memento.getChild(ATTRIBUTES_ELEMENT);
            if (attMemento != null)
            {
                String data = attMemento.getTextData();
                if (data != null)
                {
                    final AttributeValueSets sets = AttributeValueSets
                        .deserialize(new ByteArrayInputStream(data.getBytes("UTF-8")));
                    attributes = sets.getDefaultAttributeValueSet();
                }
            }
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }

        if (attributes == null)
        {
            attributes = new AttributeValueSet("defaults");
        }
        search = new SearchInput(source, algorithm, attributes);

        return search;
    }

    private String tryGetStringFrom(IMemento memento, String elementName,
        String attributeName)
    {
        IMemento child = memento.getChild(elementName);
        if (child != null)
        {
            return child.getString(attributeName);
        }
        return null;
    }

    static void saveState(SearchInput search, IMemento memento)
    {
        memento.createChild(SOURCE_ELEMENT).putString(ID_ATTRIBUTE, search.getSourceId());
        memento.createChild(ALGORITHM_ELEMENT).putString(ID_ATTRIBUTE,
            search.getAlgorithmId());
        try
        {
            final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();

            /*
             * Limit saved attributes to @Input and @Processing ones.
             */
            final Map<String, Object> actual = search.getAttributeValueSet()
                .getAttributeValues();
            final AttributeValueSet filtered = new AttributeValueSet("memento-saved");

            final BindableDescriptor input = core.getComponentDescriptor(
                search.getSourceId()).flatten().only(Input.class, Processing.class);
            for (String key : input.attributeDescriptors.keySet())
            {
                if (actual.containsKey(key))
                {
                    filtered.setAttributeValue(key, actual.get(key));
                }
            }

            final BindableDescriptor algorithm = core.getComponentDescriptor(
                search.getAlgorithmId()).flatten().only(Input.class, Processing.class);
            for (String key : algorithm.attributeDescriptors.keySet())
            {
                if (actual.containsKey(key))
                {
                    filtered.setAttributeValue(key, actual.get(key));
                }
            }

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            AttributeValueSets sets = new AttributeValueSets();
            sets.addAttributeValueSet("default", filtered);
            sets.serialize(os);
            os.close();

            memento.createChild(ATTRIBUTES_ELEMENT).putTextData(
                new String(os.toByteArray(), "UTF-8"));
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
    }
}
