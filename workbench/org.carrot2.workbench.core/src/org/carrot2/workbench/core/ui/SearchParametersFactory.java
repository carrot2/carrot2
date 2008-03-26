package org.carrot2.workbench.core.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class SearchParametersFactory implements IElementFactory
{

    private static final String CAPTION_ATTRIBUTE = "caption";
    private static final String ALGORITHM_ELEMENT = "algorithm";
    private static final String SOURCE_ELEMENT = "source";
    public static final String ID = "org.carrot2.workbench.core.searchParametersFactory";

    public IAdaptable createElement(IMemento memento)
    {
        String source = tryGetStringFrom(memento, SOURCE_ELEMENT, CAPTION_ATTRIBUTE);
        String algorithm =
            tryGetStringFrom(memento, ALGORITHM_ELEMENT, CAPTION_ATTRIBUTE);
        if (StringUtils.isBlank(source) || StringUtils.isBlank(algorithm))
        {
            return null;
        }
        SearchParameters search = new SearchParameters(source, algorithm, null);
        // TODO: add remembering of all required attributes in the same way - M2 probably
        String query = tryGetStringFrom(memento, "query", "text");
        if (StringUtils.isBlank(query))
        {
            return null;
        }
        search.putAttribute("query", query);
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

    static void saveState(SearchParameters search, IMemento memento)
    {
        memento.createChild(SOURCE_ELEMENT).putString(CAPTION_ATTRIBUTE,
            search.getSourceCaption());
        memento.createChild(ALGORITHM_ELEMENT).putString(CAPTION_ATTRIBUTE,
            search.getAlgorithmCaption());
        memento.createChild("query").putString("text",
            search.getAttributes().get("query").toString());
    }

}
