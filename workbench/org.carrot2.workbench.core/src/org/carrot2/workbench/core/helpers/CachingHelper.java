package org.carrot2.workbench.core.helpers;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.*;

import com.google.common.collect.Maps;

public class CachingHelper
{
    private static Map<Class<? extends ProcessingComponent>, CachingController> controllers =
        Maps.newHashMap();
    private static boolean disposed;

    @SuppressWarnings("unchecked")
    public synchronized static Controller getController(ProcessingComponent component)
    {
        if (disposed)
        {
            throw new IllegalStateException("All controllers have been disposed already!");
        }
        if (!controllers.containsKey(component.getClass()))
        {
            CachingController controller = new CachingController(component.getClass());
            controller.init(new HashMap<String, Object>());
            controllers.put(component.getClass(), controller);
        }
        return controllers.get(component.getClass());

    }

    public synchronized static void disposeAllControllers()
    {
        for (CachingController controller : controllers.values())
        {
            controller.dispose();
        }
        disposed = true;
    }
}
