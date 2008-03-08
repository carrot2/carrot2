package org.carrot2.workbench.core;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.*;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
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
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        Rectangle fullScreenSize = Display.getDefault().getPrimaryMonitor()
            .getClientArea();
        int width = calculateInitialSize(fullScreenSize.width, 800);
        int height = calculateInitialSize(fullScreenSize.height, 600);
        configurer.setInitialSize(new Point(width, height));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(false);
        configurer.setTitle("Carrot2 Workbench");
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
