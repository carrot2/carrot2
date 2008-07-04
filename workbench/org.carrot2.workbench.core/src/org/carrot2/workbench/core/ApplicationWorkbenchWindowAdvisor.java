package org.carrot2.workbench.core;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.*;

/**
 * Configures various aspects of the main application's window. 
 */
final class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
    /*
     * 
     */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        super(configurer);
    }

    /*
     * 
     */
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
    {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    /*
     * 
     */
    public void preWindowOpen()
    {
        final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        final Rectangle fullScreenSize =
            Display.getDefault().getPrimaryMonitor().getClientArea();
        int width = calculateInitialSize(fullScreenSize.width, 800);
        int height = calculateInitialSize(fullScreenSize.height, 600);
        configurer.setInitialSize(new Point(width, height));

        configurer.setShowStatusLine(true);
        configurer.setTitle("Carrot2 Workbench");
        configurer.setShowMenuBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowProgressIndicator(true);
        configurer.setShowCoolBar(true);
    }
    
    /*
     * 
     */
    @Override
    public void postWindowCreate()
    {
        /*
         * Manually remove text editor contributions to the coolbar.
         * TODO: Is there a nicer way to remove editor contributions in RCP? 
         */

        final Collection<String> toolbarRemoveItems = Arrays.asList(new String [] {
            "org.eclipse.ui.edit.text.actionSet.annotationNavigation",
            "org.eclipse.ui.edit.text.actionSet.navigation"
        });

        final ICoolBarManager mm = getWindowConfigurer().getActionBarConfigurer().getCoolBarManager();
        for (IContributionItem item : mm.getItems())
        {
            Logger.getLogger(this.getClass()).info("Toolbar contribution: " + item.getId());
            if (toolbarRemoveItems.contains(item.getId()))
            {
                mm.remove(item);
            }
        }
        mm.update(true);
    }

    /**
     * Calculates specified ratio of fullScreenSize (currently 80%) in such a way, that
     * result is not smaller than minSize. Calculates one coordinate only.
     * 
     * @param fullScreenSize size of full screen
     * @param minSize minimal wanted size
     * @return initial size for the workbench window
     */
    private int calculateInitialSize(int fullScreenSize, int minSize)
    {
        int size;
        final double ratio = 0.8;
        if ((int) (fullScreenSize * ratio) >= minSize)
        {
            size = (int) (fullScreenSize * ratio);
        }
        else
        {
            if (fullScreenSize >= minSize)
            {
                size = minSize;
            }
            else
            {
                size = fullScreenSize;
            }
        }
        return size;
    }
}
