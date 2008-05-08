package org.carrot2.workbench.core.ui.attributes;

import static org.eclipse.swt.SWT.*;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.part.IPageSite;

public class ExpandBarGrouppedControl implements IAttributesGrouppedControl
{

    private Composite mainControl;
    private java.util.List<AttributesPage> pages = new ArrayList<AttributesPage>();
    private ProcessingComponent component;
    private Map<String, Object> attributes;

    public void init(ProcessingComponent component, Map<String, Object> attributes)
    {
        this.component = component;
        this.attributes = attributes;
    }

    public void createGroup(Object label, AttributesControlConfiguration conf,
        IPageSite site)
    {
        final ExpandableComposite group =
            new ExpandableComposite(mainControl, SWT.NONE, ExpandableComposite.TWISTIE);
        group.setText(label.toString());
        AttributesPage page = new AttributesPage(component, attributes, conf);
        page.init(site);
        page.createControl(group);

        group.setClient(page.getControl());
        group.setExpanded(true);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
        gd.heightHint = group.computeSize(DEFAULT, DEFAULT).y;
        group.setLayoutData(gd);

        group.addExpansionListener(new ExpansionAdapter()
        {
            @Override
            public void expansionStateChanged(ExpansionEvent e)
            {
                GridData gd =
                    new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
                gd.heightHint = group.computeSize(DEFAULT, DEFAULT).y;
                group.setLayoutData(gd);
                mainControl.layout(true);
            }
        });

        pages.add(page);
    }

    public void createMainControl(Composite parent)
    {
        mainControl = new Composite(parent, V_SCROLL | H_SCROLL);
        mainControl.setLayout(new GridLayout());
    }

    public void dispose()
    {
        mainControl.dispose();
        for (AttributesPage page : pages)
        {
            page.dispose();
        }
    }

    public Control getControl()
    {
        return mainControl;
    }

    public List<AttributesPage> getPages()
    {
        return pages;
    }

}
