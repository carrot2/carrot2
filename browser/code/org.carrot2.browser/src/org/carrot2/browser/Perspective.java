package org.carrot2.browser;

import org.carrot2.browser.ui.QueryView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		layout.addStandaloneView(QueryView.ID, true, IPageLayout.LEFT, 0.5f, layout.getEditorArea());
	}
}
