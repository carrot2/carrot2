
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo.swing.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public final class SwingTask {

    private SwingTask() {
        // No instances.
    }
    
    /**
     * Attempts to invoke a task from AWT thread as soon
     * as possible (possibly immediately). 
     */
    public static void runNow(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            // safe to invoke it directly.
            task.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(task);
            } catch (InterruptedException e) {
                // just post to the queue then.
                SwingUtilities.invokeLater(task);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }        
    }
}
