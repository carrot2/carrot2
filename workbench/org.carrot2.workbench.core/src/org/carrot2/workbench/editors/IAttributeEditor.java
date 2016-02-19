
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.editors;

import java.util.Map;

import org.carrot2.util.attribute.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * An attribute editor is a visual control used to display (and possibly edit) the value
 * of a given attribute (described by an {@link AttributeDescriptor}).
 * <p>
 * The life cycle of an attribute editor is as follows:
 * <ul>
 * <li>call
 * {@link #init(BindableDescriptor, AttributeDescriptor, IAttributeEventProvider, Map)}.
 * <li>call {@link #createEditor(Composite, int)}
 * </ul>
 * Then, repeatedly:
 * <ul>
 * <li>call {@link #setValue(Object)}
 * <li>call {@link #getValue()}
 * </ul>
 * Finally or upon error in initialization:
 * <ul>
 * <li>call {@link #dispose()}
 * </ul>
 */
public interface IAttributeEditor extends IAttributeEventProvider
{
    /**
     * Initialize the editor to work with a given <code>bindable</code>'s attribute
     * 
     * @param bindable A descriptor of a {@link Bindable} object to which the attribute
     *            belongs.
     * @param attribute The attribute that the editor should display and allow editing.
     * @param eventProvider Global provider of events on all attributes of the
     *            <code>bindable</code>.
     * @param currentValues Current values of all attributes of a {@link Bindable} or an
     *            empty map.
     */
    AttributeEditorInfo init(BindableDescriptor bindable, AttributeDescriptor attribute,
        IAttributeEventProvider eventProvider, Map<String, Object> currentValues);

    /**
     * Create the editor's visual aspects using the given parent composite and the
     * provided number of columns.
     * <p>
     * The parent composite will have {@link GridLayout}, the editor <b>must</b> fill
     * the given number of columns.
     */
    void createEditor(Composite parent, int gridColumns);

    /**
     * The container request to set the focus to the internal component that should have
     * initial focus.
     */
    void setFocus();

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
     * Dispose visual components and any other resources.
     */
    void dispose();
}
