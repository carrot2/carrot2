
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.controller.loaders;

import java.util.*;

import bsh.EvalError;
import bsh.Interpreter;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalComponentFactory;


/**
 * A component factory class for instantiating components using plain
 * no-argument constructor and optionally initializing them with a snippet of
 * BeanShell code or map of default parameters.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class PlainComponentFactory implements LocalComponentFactory {
    /**
     * BeanShell initialization script for new components.
     */
    private final String initBeanShell;

    /**
     * A map of default parameters for new components.
     */
    private final Map defaults;

    /**
     * The component class to use for new components' instantiation.
     */
    private final Class componentClass;

    /**
     * Initializes the factory wrapper.
     *
     * @param componentClass Full class name of the component class. The component must have a
     *                       public parameterless constructor.
     * @param initBeanshell  A BeanShell script invoked to initialize every created component,
     *                       or <code>null</code>.
     * @param defaults       A map of parameters used to initialize every created component,
     *                       or <code>null</code>.
     */
    public PlainComponentFactory(Class componentClass, String initBeanshell,
        HashMap defaults) throws ComponentInitializationException {
        this.initBeanShell = initBeanshell;
        this.defaults = defaults;
        this.componentClass = componentClass;

        try {
            getInstance();
        } catch (Throwable t) {
            throw new ComponentInitializationException(
                "Could not instantiate component instance.", t);
        }
    }

    /**
     * Creates a new instance of a component using the wrapped factory and
     * initializes it using beanshell script/ properties provided.
     */
    public LocalComponent getInstance() {
        LocalComponent component;

        try {
            component = (LocalComponent) componentClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(
                "Could not instantiate component factory: " +
                componentClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Could not instantiate component factory because of restricted access " +
                "to class:" + componentClass.getName(), e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Class: does not implement " +
                LocalComponentFactory.class.getName() + ": " +
                componentClass.getName(), e);
        }

        // initialize with a beanshell script?
        if (this.initBeanShell != null) {
            // TODO: performance improvement?
            // maybe we could embed the initialization script within a body of a bsh function
            // and preparse it into the namespace of the interpreter? On the other hand,
            // initialization is invoked only once per component and they are reused afterwards.
            Interpreter interpreter = new Interpreter();

            try {
                interpreter.set("component", component);
                interpreter.eval(initBeanShell);
            } catch (EvalError e) {
                throw new RuntimeException("Error evaluating BeanShell initialization script.",
                    e);
            }
        }

        // initialize default properties?
        if (this.defaults != null) {
            for (Iterator i = defaults.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                component.setProperty(key, (String) defaults.get(key));
            }
        }

        return component;
    }
}
