package org.ukrukar.converter.core.editors;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.ukrukar.converter.core.views.DefaultViewerFactory;
import org.ukrukar.converter.core.views.ITextViewer;

public class MultiTextViewerEditor extends MultiPageEditorPart {

	public static final String ID="org.ukrukar.converter.core.editors.MultiTextViewerEditor";
	
	ITextViewer viewer;
	
	@Override
	protected void createPages() {
		DefaultViewerFactory factory = new DefaultViewerFactory();
		viewer = factory.createTextViewer(this.getContainer());
		this.addPage(viewer.createControl(this.getContainer()));
		this.setPageText(0, "Default");
		viewer.refreshText(this.getEditorInput().getName());
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
}
