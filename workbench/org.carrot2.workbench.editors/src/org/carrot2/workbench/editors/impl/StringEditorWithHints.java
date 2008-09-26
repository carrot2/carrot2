package org.carrot2.workbench.editors.impl;

import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.constraint.ValueHintEnum;
import org.carrot2.util.attribute.constraint.ValueHintEnumConstraint;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.BiMap;

/**
 * Editor for textual content with hints (restricted to hints only and provided as hints
 * for the string editor).
 */
public class StringEditorWithHints extends AttributeEditorAdapter
{
    /*
     * 
     */
    private Combo textBox;

    /*
     * 
     */
    private String content;

    /**
     * Value hints for the combo box.
     */
    private ValueHintEnum hint;

    /**
     * @see ValueHintEnumConstraint#getValueToFriendlyName(ValueHintEnum)
     */
    private BiMap<String, String> valueToNameMapping;
    
    /**
     * Order of hints on the suggestion list.
     */
    private Collection<String> valueOrder;
    
    

    /*
     * 
     */
    public StringEditorWithHints()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        this.hint = (ValueHintEnum) descriptor.getAnnotation(ValueHintEnum.class);
        this.valueToNameMapping = ValueHintEnumConstraint.getValueToFriendlyName(hint);
        this.valueOrder = ValueHintEnumConstraint.getValidValuesMap(hint).keySet();

        return super.init(descriptor);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        textBox = new Combo(parent, SWT.BORDER);

        textBox.setLayoutData(GUIFactory.editorGridData().grab(true, false).hint(200,
            SWT.DEFAULT).align(SWT.FILL, SWT.CENTER).span(gridColumns, 1).create());

        /*
         * React to focus lost.
         */
        textBox.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                checkContentChange();
            }
        });

        textBox.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                fireContentChange(mapToValue(textBox.getText()));
            }
        });

        textBox.addTraverseListener(new TraverseListener()
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
         * Add hints from the annotation.
         */
        for (String value : valueOrder)
        {
            textBox.add(valueToNameMapping.get(value));
        }

        this.content = null;
    }

    /*
     * 
     */
    private String mapToValue(String text)
    {
        String value = this.valueToNameMapping.inverse().get(text);
        if (value == null)
        {
            value = text;
        }
        return value;
    }

    /*
     * 
     */
    private String mapFromValue(String text)
    {
        String value = this.valueToNameMapping.get(text);
        if (value == null)
        {
            value = text;
        }
        return value;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        this.textBox.setFocus();
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return content;
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

        textBox.setText(mapFromValue(newValue == null ? "" : newValue.toString()));
        checkContentChange();
    }

    /**
     * Check if the content has changed compared to the current value of this attribute.
     * If so, fire an event.
     */
    private void checkContentChange()
    {
        final String textBoxValue = this.textBox.getText();
        if (!ObjectUtils.equals(textBoxValue, content))
        {
            this.content = mapToValue(textBoxValue);
            fireAttributeChange(new AttributeChangedEvent(this));
        }
    }
}
