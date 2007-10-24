package org.ukrukar.converter.core.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class DefaultTextViewer implements ITextViewer {

	private Label viewer;
	
	public DefaultTextViewer(Composite parent) {
	}
	
	public void refreshText(String text) {
		viewer.setText(text);
	}

	public Control createControl(Composite parent) {
		viewer = new Label(parent, SWT.HORIZONTAL | SWT.WRAP);
		viewer.setSize(100, 50);
		return viewer;
	}

	
	
}
