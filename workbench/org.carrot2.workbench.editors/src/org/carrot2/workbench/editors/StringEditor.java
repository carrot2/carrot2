package org.carrot2.workbench.editors;

import static org.eclipse.swt.SWT.BORDER;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

/**
 * Editor for textual values. Sends change events only on actual content change, committed
 * by return key traversal or focus lost event.
 */
public class StringEditor extends AttributeEditorAdapter
{
    private Text textBox;
    private String content;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        textBox = new Text(parent, BORDER);
        textBox.setLayoutData(layoutData);

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

    @Override
    public Object getValue()
    {
        return content;
    }

    @Override
    public void setValue(Object currentValue)
    {
        if (currentValue != null)
        {
            textBox.setText(currentValue.toString());
            checkContentChange();
        }
    }

    @Override
    public void saveState(IMemento memento)
    {
        memento.putTextData(getValue().toString());
    }

    @Override
    public void restoreState(IMemento memento)
    {
        setValue(memento.getTextData());
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
