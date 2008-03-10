package org.carrot2.workbench.core.ui;

import java.net.URL;

import org.carrot2.core.Document;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class DocumentWidget extends Composite
{

    private Label titleText;
    private Link titleLink;
    private Label summaryText;
    private boolean containsUrl;

    public DocumentWidget(Composite parent, int style, Document doc)
    {
        super(parent, style | SWT.BORDER);
        this
            .setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        this.setBackgroundMode(SWT.INHERIT_DEFAULT);
        containsUrl = (doc.getField(Document.CONTENT_URL) != null);
        initControls(doc);
        if (containsUrl)
        {
            titleLink.setText(String
                .format("<A HREF=\"%s\">%s</A>", doc.getField(Document.CONTENT_URL)
                    .toString(), doc.getField(Document.TITLE).toString()));
            titleLink.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent e)
                {
                    try
                    {
                        // TODO: check user preference here
                        IWebBrowser browser = PlatformUI.getWorkbench()
                            .getBrowserSupport().createBrowser(
                                IWorkbenchBrowserSupport.STATUS
                                    | IWorkbenchBrowserSupport.AS_EDITOR, null, null,
                                null);
                        browser.openURL(new URL(e.text));
                    }
                    catch (Exception ex)
                    {
                        CorePlugin.getDefault().getLog().log(
                            new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -1,
                                "Error while opening browser", ex));
                    }
                }
            });
        }
        else
        {
            titleText.setText(doc.getField(Document.TITLE).toString());
        }
        summaryText.setText(doc.getField(Document.SUMMARY).toString());
    }

    private void initControls(Document doc)
    {
        if (containsUrl)
        {
            titleLink = new Link(this, SWT.LEFT);
        }
        else
        {
            titleText = new Label(this, SWT.LEFT);
        }
        Button Button_1 = new Button(this, SWT.PUSH);
        summaryText = new Label(this, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
        GridData GridData_1 = new GridData();
        GridData GridData_2 = new GridData();
        GridData GridData_3 = new GridData();
        GridLayout GridLayout_1 = new GridLayout();

        GridData_1.grabExcessHorizontalSpace = true;
        GridData_2.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        GridData_2.widthHint = 25;
        GridData_3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_3.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_3.grabExcessVerticalSpace = true;
        GridData_3.widthHint = 490;
        GridData_3.grabExcessHorizontalSpace = true;
        GridData_3.horizontalSpan = 2;
        GridLayout_1.numColumns = 2;

        // set properties
        getTitleText().setLayoutData(GridData_1);
        // TODO: change button into toolbar (EP maybe in here)
        Button_1.setText("A");
        Button_1.setLayoutData(GridData_2);
        Button_1.setVisible(false);
        summaryText.setLayoutData(GridData_3);
        this.setLayout(GridLayout_1);
    }

    public Control getTitleText()
    {
        return containsUrl ? titleLink : titleText;
    }

    public Label getSummaryText()
    {
        return summaryText;
    }

}
