package org.carrot2.workbench.editors;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPersistableEditor;

/**
 * An attribute editor is a visual control which can be used to display and edit the value
 * of given attribute, described using an {@link AttributeDescriptor}.
 * <p>
 * The life cycle of an attribute editor is as follows:
 * <ol>
 * <li>call {@link #init(AttributeDescriptor)}, if exception is thrown -> stop.
 * <li>call {@link #createEditor(Composite, Object)}, if exception -> goto 5
 * <li>call {@link #setValue(Object)}, if exception -> goto 5
 * <li>call {@link #getValue()}
 * <li>call {@link #dispose()}
 * <ol>
 */
public interface IAttributeEditor extends IPersistableEditor
{
    void init(AttributeDescriptor descriptor);

    /**
     * Create the editor's composite using the given parent. The editor
     * must set the created composite's layout data to the given object.
     * 
     * TODO: Remove layoutData from the interface.
     */
    void createEditor(Composite parent, Object layoutData);

    /**
     * Returns the edited attribute key.
     * 
     * @see AttributeDescriptor#key
     */
    String getAttributeKey();

    /**
     * Set the editor's current value to the given object, update visual components. 
     */
    void setValue(Object currentValue);

    /**
     * Return the current editor's value.
     */
    Object getValue();

    /**
     * Dispose visual components and any other resources.
     */
    void dispose();

    /**
     * Subscribe to change events. Change events may come in rapid succession.
     */
    void addAttributeChangeListener(IAttributeListener listener);

    /**
     * Unsubscribe from change events.
     */
    void removeAttributeChangeListener(IAttributeListener listener);

    /**
     * If <code>true</code>, then the editor displays its own label. Otherwise the
     * containing component must display the edited attribute's label.
     */
    boolean containsLabel();

}
