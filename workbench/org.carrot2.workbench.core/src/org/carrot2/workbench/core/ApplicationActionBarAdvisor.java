package org.carrot2.workbench.core;

import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IIDEActionConstants;

/**
 * 
 */
final class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeOthersAction;
    private IWorkbenchAction helpContentsAction;
    private IWorkbenchAction openPreferencesAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction propertiesAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction saveAsAction;

    private IContributionItem changePerspMenuItem;
    private IContributionItem showViewMenu;

    /*
     * 
     */
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }
    
    /*
     * 
     */
    protected void makeActions(IWorkbenchWindow window)
    {
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);

        saveAsAction = ActionFactory.SAVE_AS.create(window);
        register(saveAsAction);

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

    /*
     * 
     */
    @Override
    protected void fillCoolBar(ICoolBarManager coolBar)
    {
        super.fillCoolBar(coolBar);

        /*
         * File-related toolbar actions
         */

        final IToolBarManager fileToolBar = new ToolBarManager(coolBar.getStyle());
        fileToolBar.add(saveAsAction);
        fileToolBar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

        coolBar.add(new GroupMarker(IIDEActionConstants.GROUP_FILE));
        coolBar.add(fileToolBar);
    }

    /*
     * 
     */
    protected void fillMenuBar(IMenuManager menuBar)
    {
        final MenuManager fileMenu = new MenuManager("&File", "carrot2-File");
        fileMenu.add(closeAction);
        fileMenu.add(closeAllAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAsAction);
        fileMenu.add(new Separator());
        fileMenu.add(propertiesAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        final MenuManager aboutMenu = new MenuManager("&About", "carrot2-about");
        aboutMenu.add(helpContentsAction);
        aboutMenu.add(aboutAction);

        final MenuManager changePerspMenuMgr = new MenuManager("Open Perspective");
        changePerspMenuMgr.add(changePerspMenuItem);

        MenuManager showViewMenuMgr = new MenuManager("Show view");
        showViewMenuMgr.add(showViewMenu);

        final MenuManager windowMenu = new MenuManager("&Window", "carrot2-window");
        windowMenu.add(changePerspMenuMgr);
        windowMenu.add(showViewMenuMgr);
        windowMenu.add(openPreferencesAction);

        menuBar.add(fileMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(aboutMenu);
    }
}
