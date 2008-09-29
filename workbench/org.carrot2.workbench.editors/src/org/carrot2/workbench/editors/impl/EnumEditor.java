package org.carrot2.workbench.editors.impl;

import java.util.*;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.*;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.BiMap;

/**
 * Editor for mapped values (enumerated types and unrestricted strings with enum hints).
 */
public final class EnumEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    /**
     * Special value for no-selection.
     */
    private final static String NULL_VALUE = "";

    /**
     * Mapping between attribute values and their user-friendly representations.
     */
    private BiMap<Object, String> valueToFriendlyName;

    /**
     * Order of values on the suggestion list.
     */
    private List<Object> valueOrder;

    /**
     * Mapping between attribute values and enum constants.
     */
    private Map<Object, Enum<?>> valueToEnum;

    /**
     * A {@link ComboViewer} component for displaying enum constants.
     */
    private CCombo box;

    /**
     * Current attribute value;
     */
    private Object currentValue;

    /**
     * If <code>true</code> valid value selection is required (the attribute cannot be
     * <code>null</code>).
     */
    private boolean valueRequired;

    /**
     * If <code>true</code>, the attribute can take any string value, not only those
     * listed in {@link #valueOrder} and other collection fields.
     */
    private boolean anyValueAllowed;

    /*
     * 
     */
    public EnumEditor()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        Class<? extends Enum<?>> clazz = (Class) descriptor.type;
        if (clazz.isEnum())
        {
            valueToEnum = (Map) ValueHintMappingUtils.getValidValuesMap(clazz);
            valueOrder = new ArrayList<Object>(valueToEnum.keySet());
            valueToFriendlyName = (BiMap) ValueHintMappingUtils
                .getValueToFriendlyName(clazz);
            valueRequired = (descriptor.getAnnotation(Required.class) != null);
            anyValueAllowed = false;

            return super.init(descriptor);
        }
        else if (String.class.equals(clazz))
        {
            final ValueHintEnum hint = (ValueHintEnum) descriptor
                .getAnnotation(ValueHintEnum.class);
            if (hint != null)
            {
                clazz = hint.values();

                valueToEnum = (Map) ValueHintMappingUtils.getValidValuesMap(clazz);
                valueOrder = new ArrayList<Object>(valueToEnum.keySet());
                valueToFriendlyName = (BiMap) ValueHintMappingUtils
                    .getValueToFriendlyName(clazz);
                valueRequired = (descriptor.getAnnotation(Required.class) != null);
                anyValueAllowed = true;
            }
            
            return super.init(descriptor);
        }

        throw new IllegalArgumentException("Attribute type not supported: " + descriptor);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        final int style = SWT.DROP_DOWN | SWT.BORDER | (anyValueAllowed ? 0 : SWT.READ_ONLY);
        box = new CCombo(parent, style);
        box.setLayoutData(GUIFactory.editorGridData().grab(true, false).hint(200,
            SWT.DEFAULT).align(SWT.FILL, SWT.CENTER).span(gridColumns, 1).create());

        /*
         * React to focus lost.
         */
        box.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                checkContentChange();
            }
        });

        box.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                fireContentChange(userFriendlyToValue(box.getText()));
            }
        });

        box.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    checkContentChange();
                }
            }
        });

        /*
         * Add hints.
         */
        for (Object value : valueOrder)
        {
            box.add(valueToFriendlyName.get(value));
        }

        if (!valueRequired)
        {
            /*
             * Add an artificial option to the suggestion list to clear selection.
             */
            box.add(NULL_VALUE, 0);
        }

        currentValue = null;
    }

    /**
     * Map a given attribute value to user-friendly name.
     */
    private Object userFriendlyToValue(String text)
    {
        if (text == NULL_VALUE || StringUtils.isEmpty(text))
        {
            return null;
        }

        Object value = this.valueToFriendlyName.inverse().get(text);
        if (value == null)
        {
            value = text;
        }

        return value;
    }

    /**
     * Map a given user-friendly name.
     */
    private String valueToUserFriendly(Object object)
    {
        String value = this.valueToFriendlyName.get(object);
        if (value == null)
        {
            value = NULL_VALUE;
        }
        return value;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        this.box.setFocus();
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return currentValue;
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (ObjectUtils.equals(newValue, getValue()))
        {
            return;
        }

        final String asString;
        if (newValue == null)
        {
            asString = null;
        }
        else if (newValue instanceof ValueHintMapping)
        {
            asString = ((ValueHintMapping) newValue).getAttributeValue();
        }
        else if (newValue instanceof Enum)
        {
            asString = ((Enum<?>) newValue).name();
        }
        else
        {
            asString = newValue.toString();
        }

        box.setText(valueToUserFriendly(asString));
        checkContentChange();
    }

    /**
     * Check if the content has changed compared to the current value of this attribute.
     * If so, fire an event.
     */
    private void checkContentChange()
    {
        final String textBoxValue = this.box.getText();
        final Object asValue = userFriendlyToValue(textBoxValue);

        if (!ObjectUtils.equals(currentValue, asValue))
        {
            this.currentValue = asValue;
            fireAttributeChange(new AttributeChangedEvent(this));
        }
    }
}
