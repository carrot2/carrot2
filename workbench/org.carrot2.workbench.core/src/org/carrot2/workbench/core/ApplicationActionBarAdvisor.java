
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * 
 */
final class ApplicationActionBarAdvisor extends ActionBarAdvisor
{
    private IWorkbenchAction closeAction;
    private IWorkbenchAction closeAllAction;
    private IWorkbenchAction closeOthersAction;
    private IWorkbenchAction openPreferencesAction;
    private IWorkbenchAction exitAction;
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

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        openPreferencesAction = ActionFactory.PREFERENCES.create(window);
        register(openPreferencesAction);

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

        coolBar.add(fileToolBar);
    }

    /*
     * 
     */
    protected void fillMenuBar(IMenuManager menuBar)
    {
        final MenuManager fileMenu = new MenuManager("&File", "org.carrot2.menus.file");
        fileMenu.add(closeAction);
        fileMenu.add(closeAllAction);
        fileMenu.add(new Separator());
        fileMenu.add(saveAsAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        final MenuManager aboutMenu = new MenuManager("&About", "org.carrot2.menus.about");
        aboutMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        aboutMenu.add(aboutAction);

        final MenuManager changePerspMenuMgr = new MenuManager("Open Perspective", "org.carrot2.menus.openperspective");
        changePerspMenuMgr.add(changePerspMenuItem);

        MenuManager showViewMenuMgr = new MenuManager("Show view", "org.carrot2.menus.showview");
        showViewMenuMgr.add(showViewMenu);

        final MenuManager windowMenu = new MenuManager("&Window", "org.carrot2.menus.window");
        windowMenu.add(changePerspMenuMgr);
        windowMenu.add(showViewMenuMgr);
        windowMenu.add(openPreferencesAction);

        menuBar.add(fileMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(windowMenu);
        menuBar.add(aboutMenu);
    }
}
