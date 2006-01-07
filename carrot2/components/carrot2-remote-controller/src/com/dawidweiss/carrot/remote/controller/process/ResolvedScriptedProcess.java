
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.remote.controller.process;


import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.*;
import com.dawidweiss.carrot.remote.controller.cache.Cache;
import com.dawidweiss.carrot.remote.controller.components.ComponentsLoader;
import com.dawidweiss.carrot.remote.controller.guard.QueryGuard;


/**
 * This class represents a 'resolved' process definition in a BSF language.
 */
public class ResolvedScriptedProcess
    implements ProcessDefinition
{
    private static final Logger log = Logger.getLogger(ResolvedScriptedProcess.class);
    private ProcessDescriptor descriptor;
    private String script;
    private String language;
    private ComponentsLoader componentsLoader;
    private MockController mockExecutionInfo;

    public ResolvedScriptedProcess(ComponentsLoader componentsLoader, ProcessDescriptor descriptor)
        throws IllegalArgumentException
    {
        this.descriptor = descriptor;

        if (descriptor.getProcessingScript() == null)
        {
            throw new IllegalArgumentException("Processing script does not exist.");
        }

        this.script = descriptor.getProcessingScript().getContent();
        this.language = descriptor.getProcessingScript().getLanguage();

        if (BSFManager.isLanguageRegistered(language) == false)
        {
            throw new IllegalArgumentException("Script language not supported: " + language);
        }

        try
        {
            mockExecutionInfo = analyzeUsedComponents();
        }
        catch (Throwable t)
        {
            throw new RuntimeException(
                "The script did not pass mock object test: "
                + com.dawidweiss.carrot.remote.controller.util.ExceptionHelper.getStackTrace(t)
            );
        }

        if (!mockExecutionInfo.getSentsResponse())
        {
            throw new RuntimeException(
                "Script does not send any response to diagnostic mock object invocation."
            );
        }

        this.componentsLoader = componentsLoader;
    }

    /**
     * Applies the process to a given query, isolating components and trying to figure out what is
     * going wrong with the processing.
     */
    public DebugQueryExecutionInfo applyProcessWithDebug(
        Query query, OutputStream output, Map requestParameters, Cache cache, QueryGuard queryGuard,
        HttpSession session, HttpServletRequest request, ServletContext context
    )
    {
        BSFManager manager = new BSFManager();
        manager.setClassLoader(Thread.currentThread().getContextClassLoader());

        // declare global beans.
        try
        {
            manager.declareBean(
                "query", new QueryBean(query),
                com.dawidweiss.carrot.remote.controller.process.scripted.Query.class
            );
        }
        catch (BSFException e1)
        {
            DebugQueryExecutionInfo tmp = new DebugQueryExecutionInfo(log);
            tmp.startCapturingLog4j();
            log.error("Cannot declare query bean.", e1);
            tmp.finish();
            return tmp;
        }

        DebugControllerBean controller = new DebugControllerBean(
                output, cache, queryGuard, session, request, context, componentsLoader
            );
        controller.setDoCacheInput(false);
        controller.setUseCachedInput(true);

        try
        {
            manager.declareBean(
                "controller", controller,
                com.dawidweiss.carrot.remote.controller.process.scripted.Controller.class
            );
        }
        catch (BSFException e2)
        {
            DebugControllerBean.log.error("Could not declare controller bean.", e2);
            DebugQueryExecutionInfo tmp = controller.getDebugInfo();
            tmp.finish();
            return tmp;
        }

        // execute the script.
        try
        {
            manager.exec(language, "Process script: " + descriptor.getId(), 0, 0, script);
        }
        catch (BSFException e)
        {
            Throwable t = e;
            if (e.getTargetException() != null)
            {
                t = e.getTargetException();
            }
            DebugControllerBean.log.error("Error executing script.", t);
        }
        finally
        {
            // close any opened input streams.
            controller.dispose();
        }
        DebugQueryExecutionInfo debugInfo = controller.getDebugInfo();
        debugInfo.finish();
        return debugInfo;
    }


    public void applyProcess(
        Query query, OutputStream output, Map requestParameters, Cache cache, QueryGuard queryGuard,
        HttpSession session, HttpServletRequest request, ServletContext context
    )
        throws Throwable
    {
        BSFManager manager = new BSFManager();
        manager.setClassLoader(Thread.currentThread().getContextClassLoader());

        // declare global beans.
        manager.declareBean(
            "query", new QueryBean(query),
            com.dawidweiss.carrot.remote.controller.process.scripted.Query.class
        );

        ControllerBean controller = new ControllerBean(
                output, cache, queryGuard, session, request, context, componentsLoader
            );
        controller.setDoCacheInput(false);
        controller.setUseCachedInput(true);

        manager.declareBean(
            "controller", controller,
            com.dawidweiss.carrot.remote.controller.process.scripted.Controller.class
        );

        // execute the script.
        try
        {
            manager.exec(language, "Process script: " + descriptor.getId(), 0, 0, script);
        }
        catch (BSFException e)
        {
            if (e.getTargetException() != null)
            {
                throw e.getTargetException();
            }

            throw e;
        }
        finally
        {
            // close any opened input streams.
            controller.dispose();
        }
    }


    public String getInputComponentId()
    {
        List l = this.mockExecutionInfo.getUsedInputs();

        if (l.size() > 0)
        {
            return (String) l.get(0);
        }

        return null;
    }


    public String getId()
    {
        return descriptor.getId();
    }


    public boolean isScripted()
    {
        return true;
    }

	public String getScript()
	{
		return script;
	}
	
	public String getScriptLanguage()
	{
		return language;
	}

    public String getDefaultDescription()
    {
        return descriptor.getDescription();
    }

    /**
     * @return Should return <code>true</code> if this process is not to be explicitly shown
     *         in the user interface.
     */
    public boolean isHidden() {
    	return descriptor.hasHidden() && descriptor.getHidden();
    }


    /**
     * For scripted processes this method is rather a heuristic. It may return false even if the
     * proces uses the component.
     */
    public boolean usesComponent(ComponentDescriptor descriptor)
    {
        switch (descriptor.getType())
        {
            case ComponentDescriptor.INPUT_TYPE:
                return this.mockExecutionInfo.getUsedInputs().contains(descriptor.getId());

            case ComponentDescriptor.OUTPUT_TYPE:
            case ComponentDescriptor.FILTER_TYPE:

                if (this.script.indexOf('"' + descriptor.getId() + '"') >= 0)
                {
                    return true;
                }

                return false;

            default:
                throw new RuntimeException("Unknown component type: " + descriptor.getType());
        }
    }


    /**
     * Executes the script on mock objects and analyzes the components used/ syntax etc.
     */
    protected MockController analyzeUsedComponents()
        throws Throwable
    {
        BSFManager manager = new BSFManager();
        manager.setClassLoader(Thread.currentThread().getContextClassLoader());

        // declare global beans.
        manager.declareBean(
            "query", new MockQuery(),
            com.dawidweiss.carrot.remote.controller.process.scripted.Query.class
        );

        MockController controller = new MockController();
        manager.declareBean(
            "controller", controller,
            com.dawidweiss.carrot.remote.controller.process.scripted.Controller.class
        );

        // execute the script.
        try
        {
            manager.exec(language, "Process script: " + descriptor.getId(), 0, 0, script);
        }
        catch (BSFException e)
        {
            if (e.getTargetException() != null)
            {
                throw e.getTargetException();
            }

            throw e;
        }

        return controller;
    }
}
