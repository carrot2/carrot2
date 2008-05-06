package org.carrot2.workbench.editors;

import static org.eclipse.swt.SWT.BORDER;

import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class StringEditor extends AttributeEditorAdapter
{
    private Text textBox;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        textBox = new Text(parent, BORDER);
        textBox.setLayoutData(layoutData);
        textBox.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                //enter pressed
                if (e.keyCode == 13)
                {
                    doEvent();
                }
            }
        });
    }

    private void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public Object getValue()
    {
        return textBox.getText();
    }

    @Override
    public void setValue(Object currentValue)
    {
        if (currentValue != null)
        {
            textBox.setText(currentValue.toString());
        }
    }
}
