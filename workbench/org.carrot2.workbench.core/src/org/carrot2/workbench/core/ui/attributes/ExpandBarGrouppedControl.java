package org.carrot2.workbench.core.ui.attributes;

import static org.eclipse.swt.SWT.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.*;

public class ExpandBarGrouppedControl implements IAttributesGrouppedControl
{

    private ExpandBar mainBar;
    private List<AttributesPage> pages = new ArrayList<AttributesPage>();

    public void createGroup(Object label, AttributesPage attributes)
    {
        ExpandItem item = new ExpandItem(mainBar, NONE);
        item.setText(label.toString());

        item.setControl(attributes.getControl());
        item.setHeight(attributes.getControl().computeSize(DEFAULT, DEFAULT).y);

        pages.add(attributes);
    }

    public Composite createMainControl(Composite parent)
    {
        mainBar = new ExpandBar(parent, BORDER | V_SCROLL | DOUBLE_BUFFERED);
        return mainBar;
    }

    public void dispose()
    {
        mainBar.dispose();
    }

    public Control getControl()
    {
        return mainBar;
    }

    public List<AttributesPage> getPages()
    {
        return pages;
    }

}
