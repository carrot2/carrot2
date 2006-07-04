
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

package com.dawidweiss.carrot.local.controller.loaders;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

import com.dawidweiss.carrot.local.controller.LoadedProcess;
import com.dawidweiss.carrot.local.controller.ProcessLoader;


/**
 * A {@link ProcessLoader} for creating processes from a BeanShell script. The
 * provided BeanShell script must return an instance of {@link LoadedProcess}
 * class. This class contains the essential information needed to add the
 * process to the controller.
 * 
 * <p>
 * BeanShell scripts are very versatile; the controller uses BeanShell
 * 2.0-series, which includes support for dynamic class creation.
 * </p>
 * 
 * <p>
 * An example process definition may look as shown below:
 * </p>
 * 
 * <p>
 * <pre>
 * public class MyProcess extends LocalProcessBase {
 *     public MyProcess() {
 *         super(
 *             "stub-input",
 *             "stub-output-with-required-attrs",
 *             new String [] { "stub-filter" }
 *         );
 *     }
 *     
 *     protected void beforeProcessingStartsHook(RequestContext context, LocalComponent[] components) {
 *         Map params = context.getRequestParameters();
 *         params.put("integer-key", new Integer(25));
 *         params.put("string-key", "25");
 *     }
 * 
 *     protected void afterProcessingEndedHook(RequestContext context, LocalComponent[] components) {
 *     }
 * 
 *     protected void afterProcessingStartedHook(RequestContext context, LocalComponent[] components) {
 *     }
 * };
 * 
 * // return a new instance of the scripted process.
 * return new LoadedProcess( "bshprocess", new MyProcess() );
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
public class BeanShellProcessLoader implements ProcessLoader {
    
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
     * Loads a process from the data stream. The stream is converted to
     * character data using UTF-8 encoding.
     */
    public LoadedProcess load(InputStream dataStream)
        throws IOException, InstantiationException {
        final Interpreter interpreter = new Interpreter();
        interpreter.getClassManager().reset();

        try {
            registerGlobals(this.globals, interpreter);

            final LoadedProcess process = (LoadedProcess) interpreter.eval(new InputStreamReader(
                        dataStream, "UTF-8"));
            if (process == null) {
                throw new InstantiationException(
                    "BeanShell script must return an instance of " +
                    LoadedProcess.class.getName() + ".");
            }
            return process;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 must be supported.");
        } catch (EvalError e) {
            throw new InstantiationException(
                "Exception when parsing BeanShell script: " + e.toString());
        } catch (ClassCastException e) {
            throw new InstantiationException(
                    "BeanShell script must return an instance of " +
                    LoadedProcess.class.getName() + ".");
        }
    }

    /**
     * Registers a map of variables in a beanshell interpreter.
     */
    final static void registerGlobals(Map map, Interpreter interpreter) 
        throws EvalError
    {
        if (map != null) {
            for (final Iterator i = map.entrySet().iterator(); i.hasNext();) {
                final Map.Entry entry = (Map.Entry) i.next();
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                if (key instanceof String) {
                    interpreter.set((String) key, value);
                }
            }
        }
    }
}
