
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

package org.carrot2.core.controller.loaders;

import bsh.EvalError;
import bsh.Interpreter;

import org.carrot2.core.controller.ComponentFactoryLoader;
import org.carrot2.core.controller.LoadedComponentFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * A {@link ComponentFactoryLoader} for creating component factories from a
 * BeanShell script. The provided BeanShell script must return an instance of
 * {@link LoadedComponentFactory} class. This class contains the essential
 * information needed to use the factory.
 * 
 * <p>
 * BeanShell scripts are very versatile; the controller uses BeanShell
 * 2.0-series, which includes support for dynamic class creation.
 * </p>
 * 
 * <p>
 * An example component factory script may look as shown below:
 * </p>
 * 
 * <p>
 * <pre>
 * factory = new LocalComponentFactory() {
 *     public LocalComponent getInstance() {
 * 	    LocalComponent c = new StubOutputComponent();
 * 	    c.setProperty("property", "value");
 * 		return c;
 *     }
 * };
 * 	
 * return new LoadedComponentFactory("stub-output", factory); 
 * </pre>   
 * </p>
 * 
 * <p style="color: red;">
 * <b>A bug in BeanShell 2.0b1</b> prevents scripts that declare new classes
 * (named or anonymous) from being evaluated more than once (a "duplicated
 * class definition" error is thrown). Normal scenario of using the controller
 * is to add processes only once and in such case, the scripts may declare new
 * classes that override methods of their Java super classes.
 * </p>
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class BeanShellFactoryDescriptionLoader implements ComponentFactoryLoader {
    private Map globals;

    /**
     * Sets a map of global variables registered in the
     * beanshell interpreter. Any previous value is replaced
     * with the new one.
     */
    public void setGlobals(Map globalVars) {
        this.globals = globalVars;
    }
    
    /**
     * Loads a component factory from the data stream. The stream is converted
     * to character data using UTF-8 encoding.
     */
    public LoadedComponentFactory load(InputStream dataStream) 
        throws IOException, ComponentInitializationException {
        final Interpreter interpreter = new Interpreter();
        interpreter.getClassManager().reset();

        try {
            BeanShellProcessLoader.registerGlobals(this.globals, interpreter);

            LoadedComponentFactory factory = (LoadedComponentFactory) interpreter.eval(new InputStreamReader(
                        dataStream, "UTF-8"));

            if (factory == null) {
                throw new ComponentInitializationException(
                    "BeanShell script must return an instance of" +
                    " LoadedComponentFactory.");
            }

            return factory;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 must be supported.");
        } catch (EvalError e) {
            throw new ComponentInitializationException(
                "Exception when parsing BeanShell script.", e);
        } catch (ClassCastException e) {
            throw new ComponentInitializationException(
                "BeanShell script must return an instance of" +
                " LoadedComponentFactory.");
        }
    }
}