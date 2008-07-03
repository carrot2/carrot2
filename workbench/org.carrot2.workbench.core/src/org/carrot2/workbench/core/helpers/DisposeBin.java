package org.carrot2.workbench.core.helpers;

import java.util.HashMap;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * A collection of disposable resources (of multiple types that do not share
 * a common interface).
 */
public final class DisposeBin
{
    private static abstract class Disposer<T> {
        protected final T t;

        Disposer(T t)
        {
            this.t = t;
        }

        public abstract void dispose();
    };

    private static class ResourceDisposer extends Disposer<Resource>
    {
        public ResourceDisposer(Resource r)
        {
            super(r);
        }

        public void dispose()
        {
            t.dispose();
        }
    };

    private static class FormToolkitDisposer extends Disposer<FormToolkit>
    {
        public FormToolkitDisposer(FormToolkit t)
        {
            super(t);
        }

        public void dispose()
        {
            t.dispose();
        }
    };

    private static class WidgetDisposer extends Disposer<Widget>
    {
        public WidgetDisposer(Widget t)
        {
            super(t);
        }

        public void dispose()
        {
            t.dispose();
        }
    };
    
    private final HashMap<Object, Disposer<?>> resources = new HashMap<Object, Disposer<?>>();
    private final Plugin plugin;

    public DisposeBin()
    {
        this(null);
    }
    
    public DisposeBin(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public void add(Resource... resources)
    {
        for (Resource r : resources)
        {
            this.resources.put(r, new ResourceDisposer(r));
        }
    }

    public void add(FormToolkit toolkit)
    {
        this.resources.put(toolkit, new FormToolkitDisposer(toolkit));
    }
    
    /*
     * 
     */
    public void add(Widget w)
    {
        this.resources.put(w, new WidgetDisposer(w));
    }

    public void dispose()
    {
        for (Disposer<?> disposer : resources.values())
        {
            try
            {
                disposer.dispose();
            }
            catch (Throwable e)
            {
                if (plugin != null)
                {
                    IStatus status =
                        new OperationStatus(IStatus.ERROR, 
                            plugin.getBundle().getSymbolicName(), 
                            -1, 
                            "Resource disposal failed.",
                            e);

                    plugin.getLog().log(status);
                }
            }
        }
        
        resources.clear();
    }
}
