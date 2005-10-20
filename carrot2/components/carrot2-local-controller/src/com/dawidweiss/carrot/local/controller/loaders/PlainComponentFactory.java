
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller.loaders;

import bsh.EvalError;
import bsh.Interpreter;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A component factory class for instantiating components using plain
 * no-argument constructor and optionally initializing them with a snippet of
 * BeanShell code or map of default parameters.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class PlainComponentFactory extends LocalComponentFactoryBase {
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
     * Name of this factory. 
     */
    private final String name;
    
    /**
     * Description of this factory. 
     */
    private final String description;

    /**
     * Initializes the factory wrapper.
     *
     * @param componentClass Full class name of the component class. The component must have a
     *                       public parameterless constructor.
     * @param initBeanshell  A BeanShell script invoked to initialize every created component,
     *                       or <code>null</code>.
     * @param defaults       A map of parameters used to initialize every created component,
     *                       or <code>null</code>.
     * @param name           A string with a name of this factory, or <code>null</code>.
     * @param description    A string with a description of this factory, or <code>null</code>.
     */
    public PlainComponentFactory(Class componentClass, String initBeanshell,
        HashMap defaults, String name, String description) throws ComponentInitializationException {
        this.initBeanShell = initBeanshell;
        this.defaults = defaults;
        this.componentClass = componentClass;
        this.name = name;
        this.description = description;

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
    
	/**
	 * @see com.dawidweiss.carrot.core.local.LocalComponentFactory#getDescription()
	 */
	public String getDescription() {
		return description;
	}
    
	/**
	 * @see com.dawidweiss.carrot.core.local.LocalComponentFactory#getName()
	 */
	public String getName() {
		return name;
	}
}
