package org.carrot2.workbench.editors;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * An attribute editor is a visual control which can be used to display and edit the value
 * of given attribute (described by an {@link AttributeDescriptor}).
 * <p>
 * The life cycle of an attribute editor is as follows:
 * <ol>
 * <li>call {@link #init(AttributeDescriptor)}, if exception is thrown -> stop.
 * <li>call {@link #createEditor(Composite, Object)}, if exception -> goto 5
 * <li>call {@link #setValue(Object)}
 * <li>call {@link #getValue()}
 * <li>call {@link #dispose()}
 * <ol>
 */
public interface IAttributeEditor
{
    /**
     * Initialize editor to work with a given attribute descriptor and return
     * hints for graphical layout of this editor.
     */
    AttributeEditorInfo init(AttributeDescriptor descriptor);

    /**
     * Create the editor's visual aspects using the given parent composite and the
     * provided number of columns.
     * <p>
     * The parent composite will have {@link GridLayout}, the editor <b>must</b> fill
     * the given number of columns.
     */
    void createEditor(Composite parent, int gridColumns);

    /**
     * Returns the associated {@link AttributeDescriptor}'s key.
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
     * Subscribe the <code>listener</code> to change events. Change events may come in
     * rapid succession.
     */
    void addAttributeChangeListener(IAttributeListener listener);

    /**
     * Unsubscribe the <code>listener</code> from change events.
     */
    void removeAttributeChangeListener(IAttributeListener listener);

    /**
     * Dispose visual components and any other resources.
     */
    void dispose();
}
