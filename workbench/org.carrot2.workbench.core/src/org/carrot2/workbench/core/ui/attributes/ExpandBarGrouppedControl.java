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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.IPageSite;

public class ExpandBarGrouppedControl implements IAttributesGrouppedControl
{

    private Composite mainControl;
    private java.util.List<AttributesPage> pages = new ArrayList<AttributesPage>();
    private BindableDescriptor descriptor;
    private IPageSite site;

    public void init(BindableDescriptor descriptor, IPageSite site)
    {
        this.descriptor = descriptor;
        this.site = site;
    }

    @SuppressWarnings("unchecked")
    public void createGroup(Object label)
    {
        final Section group =
            new Section(mainControl, Section.TWISTIE | Section.CLIENT_INDENT);
        group.setText(label.toString());
        group.setSeparatorControl(new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL));
        AttributesPage page =
            new AttributesPage((Class<? extends ProcessingComponent>) descriptor.type,
                descriptor.attributeGroups.get(label));
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
        for (AttributesPage page : pages)
        {
            page.dispose();
        }
    }

    public Composite getControl()
    {
        if (!mainControl.isDisposed())
        {
            return mainControl.getParent();
        }
        else
        {
            return null;
        }
    }

    public List<AttributesPage> getPages()
    {
        return pages;
    }

}
