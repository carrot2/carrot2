package org.carrot2.workbench.core;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction viewAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer)
    {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window)
    {
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        viewAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        viewAction.setText("Show view");
        register(viewAction);
    }

    protected void fillMenuBar(IMenuManager menuBar)
    {
        MenuManager hyperbolaMenu = new MenuManager("&File", "hyperbola");
        hyperbolaMenu.add(exitAction);
        MenuManager helpMenu = new MenuManager("&Help", "help");
        helpMenu.add(aboutAction);
        MenuManager windowMenu = new MenuManager("&Window", "window");
        windowMenu.add(viewAction);
        menuBar.add(hyperbolaMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
    }

}
