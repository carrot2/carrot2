package org.carrot2.workbench.core.ui;

import org.carrot2.core.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class DocumentWidget extends Composite
{

    private Text titleText;
    private Text summaryText;

    public DocumentWidget(Composite parent, int style, Document doc)
    {
        super(parent, style | SWT.BORDER);
        this
            .setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        this.setBackgroundMode(SWT.INHERIT_DEFAULT);
        initControls(doc);
        titleText.setText(doc.getField(Document.TITLE).toString());
        summaryText.setText(doc.getField(Document.SUMMARY).toString());
        // this.setSize(this.computeSize(parent.getBounds().width, SWT.DEFAULT));
    }

    private void initControls(Document doc)
    {
        // TODO: change title text into link
        titleText = new Text(this, SWT.LEFT | SWT.READ_ONLY);
        Button Button_1 = new Button(this, SWT.PUSH);
        summaryText = new Text(this, SWT.LEFT | SWT.WRAP | SWT.READ_ONLY);
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
        titleText.setLayoutData(GridData_1);
        // TODO: change button into toolbar (EP maybe in here)
        Button_1.setText("A");
        Button_1.setLayoutData(GridData_2);
        Button_1.setVisible(false);
        summaryText.setLayoutData(GridData_3);
        this.setLayout(GridLayout_1);
    }

    public Text getTitleText()
    {
        return titleText;
    }

    public Text getSummaryText()
    {
        return summaryText;
    }

}
