package org.ukrukar.converter.core.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.ukrukar.converter.core.views.DefaultViewerFactory;
import org.ukrukar.converter.core.views.ITextViewer;

public class MultiTextViewerEditor extends MultiPageEditorPart {

	public static final String ID = "org.ukrukar.converter.core.editors.MultiTextViewerEditor";

	ITextViewer viewer;

	@Override
	protected void createPages() {
		DefaultViewerFactory factory = new DefaultViewerFactory();
		viewer = factory.createTextViewer(this.getContainer());
		this.addPage(viewer.createControl(this.getContainer()));
		this.setPageText(0, "Default");
		viewer.refreshText(this.getEditorInput().getName());
		BrowserViewer browser = new BrowserViewer(this.getContainer(), 0);
		browser
				.setURL("http://kb.adobe.com/selfservice/viewContent.do?externalId=tn_15507");
		this.addPage(browser);
		this.setPageText(1, "Browser");
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		this.setPartName(input.getName());
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
