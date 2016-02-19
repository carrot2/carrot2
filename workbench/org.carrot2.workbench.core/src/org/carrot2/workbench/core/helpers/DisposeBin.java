
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

package org.carrot2.workbench.core.helpers;

import java.util.ArrayList;
import java.util.HashMap;

import org.carrot2.workbench.editors.IAttributeEventProvider;
import org.carrot2.workbench.editors.IAttributeListener;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A collection of disposable resources (of multiple types that do not share a common
 * interface). The dispose bin can also register various kinds of listeners and unregister
 * them automatically at {@link #dispose()}.
 */
public final class DisposeBin
{
    private static abstract class Disposer<T>
    {
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

    private static class ActionDisposer extends Disposer<IWorkbenchAction>
    {
        public ActionDisposer(IWorkbenchAction t)
        {
            super(t);
        }

        public void dispose()
        {
            t.dispose();
        }
    };

    private static class ListenerPair
    {
        public final Object registrar;
        public final Object listener;

        public ListenerPair(Object registrar, Object listener)
        {
            this.registrar = registrar;
            this.listener = listener;
        }
    }

    /*
     * 
     */
    private final HashMap<Object, Disposer<?>> resources = new HashMap<Object, Disposer<?>>();

    /*
     * 
     */
    private final ArrayList<ListenerPair> listeners = Lists.newArrayList();

    /*
     * 
     */
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

    public void add(Widget w)
    {
        this.resources.put(w, new WidgetDisposer(w));
    }

    public void add(IWorkbenchAction action)
    {
        this.resources.put(action, new ActionDisposer(action));
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
                    IStatus status = new OperationStatus(IStatus.ERROR, plugin
                        .getBundle().getSymbolicName(), -1, "Resource disposal failed.",
                        e);

                    plugin.getLog().log(status);
                }
            }
        }

        resources.clear();

        for (ListenerPair p : listeners)
        {
            try
            {
                if (p.registrar instanceof IPreferenceStore)
                {
                    ((IPreferenceStore) p.registrar)
                        .removePropertyChangeListener((IPropertyChangeListener) p.listener);
                }
                else if (p.registrar instanceof IAttributeEventProvider)
                {
                    ((IAttributeEventProvider) p.registrar)
                        .removeAttributeListener((IAttributeListener) p.listener);
                }
                else if (p.registrar instanceof IPostSelectionProvider)
                {
                    ((IPostSelectionProvider) p.registrar)
                        .removePostSelectionChangedListener((ISelectionChangedListener) p.listener);
                }
                else
                {
                    throw new RuntimeException("Unhandled registrar: " + p.registrar);
                }
            }
            catch (Throwable t)
            {
                if (plugin != null)
                {
                    IStatus status = new OperationStatus(IStatus.ERROR, plugin
                        .getBundle().getSymbolicName(), -1, "Listener disposal failed.",
                        t);

                    plugin.getLog().log(status);
                }
            }
        }

        listeners.clear();
    }

    /*
     * 
     */
    public void registerPropertyChangeListener(IPreferenceStore provider,
        IPropertyChangeListener l)
    {
        provider.addPropertyChangeListener(l);
        listeners.add(new ListenerPair(provider, l));
    }

    /*
     * 
     */
    public void registerAttributeChangeListener(IAttributeEventProvider provider,
        IAttributeListener l)
    {
        provider.addAttributeListener(l);
        listeners.add(new ListenerPair(provider, l));
    }

    /*
     * 
     */
    public void registerPostSelectionChangedListener(IPostSelectionProvider searchEditor,
        ISelectionChangedListener l)
    {
        searchEditor.addPostSelectionChangedListener(l);
    }
}
