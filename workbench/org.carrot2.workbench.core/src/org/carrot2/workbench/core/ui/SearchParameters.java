package org.carrot2.workbench.core.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class SearchParameters implements IEditorInput
{

    private String sourceCaption;
    private String algorithmCaption;
    private Map<String, Object> attributes;

    public SearchParameters(String sourceCaption, String algorithmCaption,
        Map<String, Object> attributes)
    {
        if (sourceCaption == null || sourceCaption.length() == 0)
        {
            throw new NullArgumentException("sourceCaption");
        }
        if (algorithmCaption == null || sourceCaption.length() == 0)
        {
            throw new NullArgumentException("algorithmCaption");
        }
        this.sourceCaption = sourceCaption;
        this.algorithmCaption = algorithmCaption;
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

    public String getSourceCaption()
    {
        return sourceCaption;
    }

    public String getAlgorithmCaption()
    {
        return algorithmCaption;
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
        return null;
    }

    public String getName()
    {
        return "SearchParameters";
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public String getToolTipText()
    {
        return "SearchParameters ToolTip";
    }

    public Object getAdapter(Class adapter)
    {
        return null;
    }

}
