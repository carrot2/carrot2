package carrot2.demo.swing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class SwingTask {
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
