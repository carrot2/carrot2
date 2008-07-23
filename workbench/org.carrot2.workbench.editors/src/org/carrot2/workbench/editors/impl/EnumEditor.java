package org.carrot2.workbench.editors.impl;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Editor for enumerated types, rendered as a combo box.
 */
public final class EnumEditor extends AttributeEditorAdapter implements IAttributeEditor
{
    /**
     * The edited enum constants.
     */
    private Object [] constants;
    
    /**
     * Combo viewer used to display these constants.
     */
    private ComboViewer viewer;

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
    @Override
    public AttributeEditorInfo init(AttributeDescriptor descriptor)
    {
        constants = descriptor.type.getEnumConstants();
        return super.init(descriptor);
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        viewer = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                final String text = ((Enum<?>) element).toString();
                return text;
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                fireAttributeChange(new AttributeChangedEvent(EnumEditor.this));
            }
        });

        viewer.setInput(constants);
        viewer.getCombo().setLayoutData(
            GUIFactory.editorGridData()
                .grab(true, false)
                .span(gridColumns, 1).create());
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (newValue == null || newValue.equals(getValue()))
        {
            return;
        }

        viewer.setSelection(new StructuredSelection(newValue));
        fireAttributeChange(new AttributeChangedEvent(this));
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        final int selectionIndex = viewer.getCombo().getSelectionIndex();

        if (selectionIndex == -1)
        {
            return null;
        }
        else
        {
            return constants[selectionIndex];
        }
    }
}
