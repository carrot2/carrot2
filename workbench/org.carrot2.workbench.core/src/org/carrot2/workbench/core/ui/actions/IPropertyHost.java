package org.carrot2.workbench.core.ui.actions;

import org.eclipse.jface.util.IPropertyChangeListener;

public interface IPropertyHost
{

    void setProperty(String key, String value);

    String getProperty(String key);

    void addPropertyChangeListener(IPropertyChangeListener listener);

    void removePropertyChangeListener(IPropertyChangeListener listener);

}
