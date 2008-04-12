package org.carrot2.workbench.editors;

import java.util.EventListener;

public interface AttributeChangeListener extends EventListener
{

    void attributeChange(AttributeChangeEvent event);

}
