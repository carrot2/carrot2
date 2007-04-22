package org.carrot2.browser.ui;

import org.carrot2.browser.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class QueryView extends ViewPart {
	
	public static String ID = "org.carrot2.browser.query"; //$NON-NLS-1$

	public void createPartControl(Composite parent) {
		FormLayout layout = new FormLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		parent.setLayout(layout);
		
		Font font = parent.getDisplay().getSystemFont();
		
		Label queryLabel = new Label(parent, SWT.LEAD);
		queryLabel.setText(Messages.getString("queryview.query")); //$NON-NLS-1$
		queryLabel.setFont(font);
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0, 5);
		labelData.top = new FormAttachment(0, 5);
		queryLabel.setLayoutData(labelData);
		
		Text queryText = new Text(parent, SWT.LEAD | SWT.BORDER);
		queryText.setFont(font);
		FormData textData = new FormData();
		textData.left = new FormAttachment(queryLabel, 5);
		textData.top = new FormAttachment(0, 5);
		textData.right = new FormAttachment(100, -5);
		queryText.setLayoutData(textData);
		
		Button submitButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		submitButton.setText(Messages.getString("queryview.submit")); //$NON-NLS-1$
		submitButton.setFont(font);
		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(queryText, 0, SWT.LEFT);
		buttonData.top = new FormAttachment(queryText, 5);
		submitButton.setLayoutData(buttonData);
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
