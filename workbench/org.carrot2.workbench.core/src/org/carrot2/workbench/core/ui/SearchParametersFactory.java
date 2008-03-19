package org.carrot2.workbench.core.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class SearchParametersFactory implements IElementFactory
{

    public static final String ID = "org.carrot2.workbench.core.searchParametersFactory";

    public IAdaptable createElement(IMemento memento)
    {
        String source = memento.getChild("source").getString("caption");
        String algorithm = memento.getChild("algorithm").getString("caption");
        SearchParameters search = new SearchParameters(source, algorithm, null);
        search.putAttribute("query", memento.getChild("query").getString("text"));
        return search;
    }

}
