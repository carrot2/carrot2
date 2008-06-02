package org.carrot2.workbench.core.ui.attributes;

import java.util.List;

import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * Implementing classes create grouping control, that {@link AttributesPage} can be put
 * on.
 * <p>
 * Group has to fire {@link AttributeChangeEvent}s, when value of a attribute is changed
 * in {@link AttributesPage}.
 * </p>
 * 
 * @see BindableDescriptor#group(org.carrot2.util.attribute.BindableDescriptor.GroupingMethod)
 */
public interface IAttributesGrouppedControl
{
    void init(BindableDescriptor descriptor);

    /**
     * Creates control, that will store one group only.
     * <p>
     * Each group should be places on a main control.
     * </p>
     * 
     * @param label group label
     * @see BindableDescriptor#attributeGroups
     */
    void createGroup(Object label);

    /**
     * Creates control, that will store attributes outside of any group.
     */
    void createOthers();

    /**
     * Creates one main control, that all the groups will be placed on.
     * 
     * @param parent the parent control, which should be used as the parent for all
     *            attribute pages
     * 
     */
    void createMainControl(Composite parent);

    void setAttributeValue(String key, Object value);

    /**
     * @return root control of a groupped control
     */
    Composite getControl();

    List<AttributesPage> getPages();

    void dispose();

    public void addAttributeChangeListener(AttributeChangeListener listener);

    public void removeAttributeChangeListener(AttributeChangeListener listener);

}
