package org.carrot2.browser.ui;

import org.carrot2.browser.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class QueryView extends ViewPart {
	
	public static String ID = "org.carrot2.browser.query"; //$NON-NLS-1$

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
        layout.numColumns = 2;
		parent.setLayout(layout);
		
		Font font = parent.getDisplay().getSystemFont();
        
        Label queryLabel = new Label(parent, SWT.LEFT);
		queryLabel.setText(Messages.getString("queryview.query")); //$NON-NLS-1$
		queryLabel.setFont(font);
		queryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
        
        Text queryText = new Text(parent, SWT.LEFT | SWT.BORDER);
		queryText.setFont(font);
		queryText.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        
        Label sourceLabel = new Label(parent, SWT.LEFT);
        sourceLabel.setText(Messages.getString("queryview.source")); //$NON-NLS-1$
		sourceLabel.setFont(font);
        sourceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
        
        Combo sourceCombo = new Combo(parent, SWT.LEFT | SWT.BORDER);
        sourceCombo.setFont(font);
        sourceCombo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        
        Label resultsLabel = new Label(parent, SWT.LEFT);
        resultsLabel.setText(Messages.getString("queryview.results")); //$NON-NLS-1$
        resultsLabel.setFont(font);
        resultsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
        
        Combo resultsCombo = new Combo(parent, SWT.LEFT | SWT.BORDER);
        resultsCombo.setFont(font);
        resultsCombo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        
        Label algoLabel = new Label(parent, SWT.LEFT);
        algoLabel.setText(Messages.getString("queryview.algorithm")); //$NON-NLS-1$
        algoLabel.setFont(font);
        algoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false));
        
        Combo algoCombo = new Combo(parent, SWT.LEFT | SWT.BORDER);
        algoCombo.setFont(font);
        algoCombo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
        
		Button submitButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		submitButton.setText(Messages.getString("queryview.submit")); //$NON-NLS-1$
		submitButton.setFont(font);
        GridData data = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
        data.horizontalSpan = 2;
		submitButton.setLayoutData(data);
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
