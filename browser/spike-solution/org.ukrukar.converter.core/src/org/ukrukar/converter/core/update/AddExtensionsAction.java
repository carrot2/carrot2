package org.ukrukar.converter.core.update;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

public class AddExtensionsAction extends Action {

	public static final String ID = "org.ukrukar.converter.core.update.AddExtensionsAction";
	private IWorkbenchWindow window;

	public AddExtensionsAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Search for Extensions...");
	}

	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
			public void run() {
				UpdateJob job = new UpdateJob("Search for new extensions",
						getSearchRequest());
				UpdateManagerUI.openInstaller(window.getShell(), job);
			}
		});
	}

	private UpdateSearchRequest getSearchRequest() {
		UpdateSearchRequest result = new UpdateSearchRequest(
				UpdateSearchRequest.createDefaultSiteSearchCategory(),
				new UpdateSearchScope());
		//result.addFilter(new BackLevelFilter());
		//result.addFilter(new EnvironmentFilter());
		UpdateSearchScope scope = new UpdateSearchScope();
		try {
			String homeBase = "http://localhost:8000/Algorithms-site/";
			URL url = new URL(homeBase);
			scope.addSearchSite("Converter site", url, null);
		} catch (MalformedURLException e) {
			// skip bad URLs
		}
		result.setScope(scope);
		return result;
	}

}
