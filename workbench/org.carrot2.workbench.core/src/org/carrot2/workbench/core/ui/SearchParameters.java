package org.carrot2.workbench.core.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;

public class SearchParameters implements IEditorInput, IPersistableElement
{

    private String sourceId;
    private String algorithmId;
    private Map<String, Object> attributes;

    public SearchParameters(String sourceId, String algorithmId,
        Map<String, Object> attributes)
    {
        if (StringUtils.isBlank(sourceId))
        {
            throw new NullArgumentException("sourceId");
        }
        if (StringUtils.isBlank(algorithmId))
        {
            throw new NullArgumentException("algorithmId");
        }
        this.sourceId = sourceId;
        this.algorithmId = algorithmId;
        if (attributes == null)
        {
            this.attributes = new HashMap<String, Object>();
        }
        else
        {
            this.attributes = attributes;
        }
    }

    public void putAttribute(String key, Object value)
    {
        this.attributes.put(key, value);
    }

    public Object removeAttribute(String key)
    {
        return this.attributes.remove(key);
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public String getAlgorithmId()
    {
        return algorithmId;
    }

    public Map<String, Object> getAttributes()
    {
        return attributes;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return ComponentLoader.SOURCE_LOADER.getComponent(this.sourceId).getIcon();
    }

    public String getName()
    {
        return "SearchParameters";
    }

    public IPersistableElement getPersistable()
    {
        return this;
    }

    public String getToolTipText()
    {
        return "SearchParameters ToolTip";
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        if (adapter.isInstance(this))
        {
            return this;
        }
        return null;
    }

    public String getFactoryId()
    {
        return SearchParametersFactory.ID;
    }

    public void saveState(IMemento memento)
    {
        SearchParametersFactory.saveState(this, memento);
    }
}
