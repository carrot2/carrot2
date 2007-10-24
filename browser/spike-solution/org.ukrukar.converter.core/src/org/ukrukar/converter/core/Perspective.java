package org.ukrukar.converter.core;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.ukrukar.converter.core.views.QuestionView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		
		layout.addStandaloneView(QuestionView.ID,  false, IPageLayout.LEFT, 1.0f, editorArea);
		layout.setEditorAreaVisible(true);
	}

}
