package org.carrot2.workbench.editors.impl;

import static org.eclipse.swt.SWT.BORDER;

import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Editor for textual content. Sends
 * {@link IAttributeListener#attributeChange(AttributeChangedEvent)} events only on actual
 * content change, committed by return key traversal or focus lost event. On-keystroke
 * content change is propagated via
 * {@link IAttributeListener#contentChanging(IAttributeEditor, Object value)}.
 */
public class StringEditor extends AttributeEditorAdapter
{
    /*
     * 
     */
    private Text textBox;
    
    /*
     * 
     */
    private String content;

    /*
     * 
     */
    public StringEditor()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        textBox = new Text(parent, BORDER);
        textBox.setLayoutData(GUIFactory.editorGridData()
            .grab(true, false)
            .span(gridColumns, 1).create());

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
                fireContentChange(textBox.getText());
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

        this.content = textBox.getText();
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
    public void setValue(Object currentValue)
    {
        if (currentValue != null)
        {
            textBox.setText(currentValue.toString());
            checkContentChange();
        }
    }

    /**
     * Check if the content has changed compared to the current value of this attribute.
     * If so, fire an event.
     */
    private void checkContentChange()
    {
        final String textBoxValue = this.textBox.getText();
        if (this.content == null || !this.content.equals(textBoxValue))
        {
            this.content = textBoxValue;
            fireAttributeChange(new AttributeChangedEvent(this));
        }
    }
}
