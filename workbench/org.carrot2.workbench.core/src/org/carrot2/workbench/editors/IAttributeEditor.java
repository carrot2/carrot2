package org.carrot2.workbench.editors;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPersistableEditor;

/**
 * Attribute editor is a control, that is used to edit and display value of given
 * attribute. The lifecycle of an attribute editor is as follows:
 * <ol>
 * <li>call editor.init(descriptor), if exception is thrown -> stop.
 * <li>call editor.createEditor(parent), if exception -> goto 5
 * <li>call editor.setValue(currentValue), if exception -> goto 5
 * <li>call editor.getValue()
 * <li>call editor.dispose()
 * <ol>
 */
public interface IAttributeEditor extends IPersistableEditor
{

    void init(AttributeDescriptor descriptor);

    String getAttributeKey();

    void createEditor(Composite parent, Object layoutData);

    void setValue(Object currentValue);

    Object getValue();

    void dispose();

    void addAttributeChangeListener(IAttributeListener listener);

    void removeAttributeChangeListener(IAttributeListener listener);

    /**
     * If false, than surrounding view must display label for given attribute on it's own.
     * If true, that label is a part of editor's control.
     * 
     * @return
     */
    boolean containsLabel();

}
