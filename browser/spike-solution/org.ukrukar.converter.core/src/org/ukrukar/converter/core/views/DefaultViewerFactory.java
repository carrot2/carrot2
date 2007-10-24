package org.ukrukar.converter.core.views;

import org.eclipse.swt.widgets.Composite;

public class DefaultViewerFactory implements ITextViewerFactory {

	public ITextViewer createTextViewer(Composite parent) {
		return new DefaultTextViewer(parent);
	}

}
