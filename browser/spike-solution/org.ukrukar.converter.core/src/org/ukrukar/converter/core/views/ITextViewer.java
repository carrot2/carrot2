package org.ukrukar.converter.core.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public interface ITextViewer {

	void refreshText(String text);
	
	Control createControl(Composite parent);
	
}
