package org.carrot2.workbench.core.ui;

import java.util.Collection;

import org.carrot2.core.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;

public class DocumentListComponent
{
    private Font boldFont;
    private Composite list;

    public Composite createControls(final Composite parent, Collection<Document> documents)
    {
        final ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
        scroll.getVerticalBar().setIncrement(5);
        scroll.setLayout(new FillLayout());
        list = new Composite(scroll, SWT.NONE);
        list.setLayout(new GridLayout());
        createRows(documents);
        scroll.setContent(list);
        // TODO: it has the right size after first resize, have to change it somehow
        list.setSize(list.computeSize(scroll.getClientArea().width, SWT.DEFAULT));
        list.layout();
        scroll.addControlListener(new ControlAdapter()
        {

            @Override
            public void controlResized(ControlEvent e)
            {
                list.setSize(list.computeSize(scroll.getClientArea().width, SWT.DEFAULT));
                list.layout();
            }
        });
        scroll.addDisposeListener(new DisposeListener()
        {

            public void widgetDisposed(DisposeEvent e)
            {
                if (boldFont != null)
                {
                    boldFont.dispose();
                }
            }

        });
        return scroll;
        // return list;
    }

    private void createRows(Collection<Document> documents)
    {
        for (Document document : documents)
        {
            DocumentWidget dw = new DocumentWidget(list, SWT.DOUBLE_BUFFERED, document);
            GridData gd = new GridData();
            gd.grabExcessHorizontalSpace = true;
            gd.horizontalAlignment = SWT.FILL;
            dw.setLayoutData(gd);
            // TODO: think of creating this fontBold differently, I don't like it this way
            if (boldFont == null)
            {
                FontData fd = dw.getTitleText().getFont().getFontData()[0];
                fd.setStyle(SWT.BOLD);
                boldFont = new Font(null, fd);
            }
            dw.getTitleText().setFont(boldFont);
        }
    }
}
