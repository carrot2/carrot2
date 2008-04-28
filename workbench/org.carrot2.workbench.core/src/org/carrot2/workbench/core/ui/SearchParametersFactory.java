package org.carrot2.workbench.core.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class SearchParametersFactory implements IElementFactory
{

    private static final String ID_ATTRIBUTE = "id";
    private static final String ALGORITHM_ELEMENT = "algorithm";
    private static final String SOURCE_ELEMENT = "source";
    public static final String ID = "org.carrot2.workbench.core.searchParametersFactory";

    public IAdaptable createElement(IMemento memento)
    {
        String source = tryGetStringFrom(memento, SOURCE_ELEMENT, ID_ATTRIBUTE);
        String algorithm = tryGetStringFrom(memento, ALGORITHM_ELEMENT, ID_ATTRIBUTE);
        if (StringUtils.isBlank(source) || StringUtils.isBlank(algorithm))
        {
            return null;
        }
        SearchParameters search = new SearchParameters(source, algorithm, null);
        // TODO: add remembering of all required attributes in the same way - M2 probably
        try
        {
            IMemento attMemento = memento.getChild("attributes");
            String hexData = attMemento.getTextData();
            if (hexData != null)
            {
                byte [] bytes = Hex.decodeHex(hexData.toCharArray());
                ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
                AttributeValueSets sets = AttributeValueSets.deserialize(stream);
                AttributeValueSet set = sets.getAttributeValueSet("attributes");
                search.putAllAttributes(set.getAttributeValues());
            }
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
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

    @SuppressWarnings("unchecked")
    static void saveState(SearchParameters search, IMemento memento)
    {
        memento.createChild(SOURCE_ELEMENT).putString(ID_ATTRIBUTE, search.getSourceId());
        memento.createChild(ALGORITHM_ELEMENT).putString(ID_ATTRIBUTE,
            search.getAlgorithmId());
        try
        {
            ProcessingComponent source =
                ComponentLoader.SOURCE_LOADER
                    .getExecutableComponent(search.getSourceId());
            BindableDescriptor desc =
                BindableDescriptorBuilder.buildDescriptor(source).flatten().only(
                    Input.class, Processing.class);
            AttributeValueSet set = new AttributeValueSet("attributes");
            for (String key : desc.attributeDescriptors.keySet())
            {
                if (search.getAttributes().containsKey(key))
                {
                    set.setAttributeValue(key, search.getAttributes().get(key));
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            AttributeValueSets sets = new AttributeValueSets();
            sets.addAttributeValueSet("attributes", set);
            sets.serialize(stream);
            memento.createChild("attributes").putTextData(
                new String(Hex.encodeHex(stream.toByteArray())));
        }
        catch (Exception e)
        {
            Utils.logError(e, false);
        }
    }

}
