package org.carrot2.workbench.core;

import org.eclipse.jface.action.*;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeOthersAction;
    private IWorkbenchAction helpContentsAction;
    private IWorkbenchAction openPreferencesAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction aboutAction;
    private IContributionItem changePerspMenuItem;
    private IContributionItem showViewMenu;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window)
    {
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        closeAction = ActionFactory.CLOSE.create(window);
        register(closeAction);

        closeAllAction = ActionFactory.CLOSE_ALL.create(window);
        register(closeAllAction);

        closeOthersAction = ActionFactory.CLOSE_OTHERS.create(window);
        register(closeOthersAction);

        helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
        register(helpContentsAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        openPreferencesAction = ActionFactory.PREFERENCES.create(window);
        register(openPreferencesAction);

        propertiesAction = ActionFactory.PROPERTIES.create(window);
        register(propertiesAction);

        changePerspMenuItem =
            ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);

        showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar)
    {
        MenuManager hyperbolaMenu = new MenuManager("&File", "carrot2-File");
        hyperbolaMenu.add(closeAction);
        hyperbolaMenu.add(closeAllAction);
        hyperbolaMenu.add(propertiesAction);
        hyperbolaMenu.add(exitAction);
        MenuManager helpMenu = new MenuManager("&About", "carrot2-about");
        helpMenu.add(helpContentsAction);
        helpMenu.add(aboutAction);
        MenuManager windowMenu = new MenuManager("&Window", "carrot2-window");
        {
            MenuManager changePerspMenuMgr = new MenuManager("Open Perspective"); //$NON-NLS-1$
            changePerspMenuMgr.add(changePerspMenuItem);
            windowMenu.add(changePerspMenuMgr);
        }
        {
            MenuManager showViewMenuMgr = new MenuManager("Show view"); //$NON-NLS-1$
            showViewMenuMgr.add(showViewMenu);
            windowMenu.add(showViewMenuMgr);
        }
        windowMenu.add(openPreferencesAction);
        menuBar.add(hyperbolaMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
    }

}
