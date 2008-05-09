package org.carrot2.workbench.core.ui.attributes;

import static org.eclipse.swt.SWT.DEFAULT;
import static org.eclipse.swt.SWT.NONE;

import java.util.ArrayList;
import java.util.List;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.BindableDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
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

    public void init(ProcessingComponent component)
    {
        this.component = component;
    }

    public void createGroup(Object label, BindableDescriptor bindableDescriptor,
        IPageSite site)
    {
        final ExpandableComposite group =
            new ExpandableComposite(mainControl, SWT.NONE, ExpandableComposite.TWISTIE);
        group.setText(label.toString());
        AttributesPage page = new AttributesPage(component,
            bindableDescriptor.attributeGroups.get(label));
        page.init(site);
        page.createControl(group);

        group.setClient(page.getControl());
        group.setExpanded(true);
        GridData gd = new GridData(GridData.FILL, GridData.FILL, true, false);
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
        mainControl.setSize(mainControl.computeSize(DEFAULT, DEFAULT));
        pages.add(page);
    }

    public void createMainControl(Composite parent)
    {
        ScrolledComposite scroll =
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroll.setLayout(new FillLayout());
        mainControl = new Composite(scroll, NONE);
        scroll.setContent(mainControl);
        mainControl.setLayout(new GridLayout());
    }

    public void dispose()
    {
        getControl().dispose();
        for (AttributesPage page : pages)
        {
            page.dispose();
        }
    }

    public Control getControl()
    {
        return mainControl.getParent();
    }

    public List<AttributesPage> getPages()
    {
        return pages;
    }

}
