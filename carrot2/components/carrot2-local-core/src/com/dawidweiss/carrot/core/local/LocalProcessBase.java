
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

package com.dawidweiss.carrot.core.local;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Provides a reference implementation of a  {@link LocalProcess} interface.
 * The reference implementation is complete (i.e. is a concrete class that can
 * be used out-of-the-box), but also contains many hooks to facilitate
 * customization for specific needs.
 *
 * @author Dawid Weiss
 * @version $Revision$
 *
 * @see LocalProcess
 */
public class LocalProcessBase implements LocalProcess {
    /**
     * An identifier of the input component's factory.
     */
    private String input;

    /**
     * An identifier of the output component's factory.
     */
    private String output;

    /**
     * A list of identifiers of filter components' factories.
     */
    private ArrayList filters = new ArrayList();
    
    /**
     * Name of this process, or <code>null</code>
     */
    private String name;
    
    /**
     * Description of this process, or <code>null</code>
     */
    private String description;

    /**
     * If <code>true</code>, the process has been initialized.
     *
     * @see #initialize(LocalControllerContext)
     */
    private boolean initialized;

    /**
     * Creates a new LocalProcessBase object. Input, output and filter
     * component should be initialized using  setter methods.
     */
    public LocalProcessBase() {
    }

    /**
     * Creates a new LocalProcessBase object, initializing identifiers for the
     * input, output and filter components.
     *
     * @param input An identifier of the input component's factory.
     * @param output An identifier of the output component's factory.
     * @param filters A list of identifiers of filter components' factories.
     *        The filters are ordered first-to-last. The first filter will be
     *        a successor of the input component, the last filter will be a
     *        predecessor of the output component.
     */
    public LocalProcessBase(String input, String output, String[] filters) {
        this(input, output, filters, null, null);
    }

    /**
     * Creates a new LocalProcessBase object, initializing identifiers for the
     * input, output and filter components.
     *
     * @param input An identifier of the input component's factory.
     * @param output An identifier of the output component's factory.
     * @param filters A list of identifiers of filter components' factories.
     *        The filters are ordered first-to-last. The first filter will be
     *        a successor of the input component, the last filter will be a
     *        predecessor of the output component.
     */
    public LocalProcessBase(String input, String output, String[] filters, 
                            String name, String description) {
        this.input = input;
        this.output = output;
        this.filters.addAll(Arrays.asList(filters));
        this.name = name;
        this.description = description;
    }
    
    /**
     * Sets an identifier of the input component factory to use for
     * instantiating components.
     *
     * @param input An identifier of the input component's factory.
     */
    public final void setInput(String input) {
        if (this.input != null) {
            throw new RuntimeException("Only one input is allowed.");
        }

        this.input = input;
    }

    /**
     * Sets an identifier of the output component's factory.
     *
     * @param output An identifier of the output component's factory.
     */
    public final void setOutput(String output) {
        if (this.output != null) {
            throw new RuntimeException("Only one output is allowed.");
        }

        this.output = output;
    }

    /**
     * Adds an identifier of a filter component's factory at the end of  the
     * filters list.
     *
     * @param filter An identifier of the filter component's factory.
     */
    public final void addFilter(String filter) {
        this.filters.add(filter);
    }

    /**
     * Initializes the local process: checks for availability of component
     * factories, and verifies pairwise component compatibility.
     * 
     * <p>
     * If this method returns successfully, the process is ready to accept
     * queries.
     * </p>
     *
     * @throws An exception is thrown if any error occurred (a component
     *         factory is not available for some component, components are not
     *         compatible).
     *
     * @see LocalProcess#initialize(LocalControllerContext)
     */
    public void initialize(LocalControllerContext context)
        throws Exception {
        if (initialized) {
            throw new RuntimeException("Object already initialized.");
        }

        if (this.input == null) {
            throw new RuntimeException("Input is required.");
        }

        if (this.output == null) {
            throw new RuntimeException("Output is required.");
        }

        // verify implemented interfaces.
        Class c;
        c = LocalInputComponent.class;

        if (!c.isAssignableFrom(context.getComponentClass(input))) {
            throw new Exception("Input component must implement: " +
                c.getName());
        }

        c = LocalOutputComponent.class;

        if (!c.isAssignableFrom(context.getComponentClass(output))) {
            throw new Exception("Output component must implement: " +
                c.getName());
        }

        c = LocalFilterComponent.class;

        for (int i = 0; i < filters.size(); i++) {
            if (!c.isAssignableFrom(context.getComponentClass(
                            (String) filters.get(i)))) {
                throw new Exception("Filter component must implement: " +
                    c.getName());
            }
        }

        // verify compatibility in pairs.
        for (int i = -1; i < filters.size(); i++) {
            String from;
            String to;

            if (i == -1) {
                from = input;
            } else {
                from = (String) filters.get(i);
            }

            if ((i + 1) == filters.size()) {
                to = output;
            } else {
                to = (String) filters.get(i + 1);
            }

            if (!context.isComponentSequenceCompatible(from, to)) {
                throw new Exception("Components not pairwise compatible: " +
                    from + ", " + to + ".\nExplanation: " +
                    context.explainIncompatibility(from, to));
            }
        }

        this.initialized = true;
    }

    /**
     * Processes a single query. The default implementation obeys the contract
     * on method order execution specified in {@link LocalProcess} interface.
     * In addition to that, it provides sever <i>hooks</i> -- protected
     * convenience methods invoked at certain points during the process of
     * query execution:
     * 
     * <ul>
     * <li>
     * {@link #getComponentInstancesForRequest(RequestContext)} This method
     * returns an ordered array of components used for query processing. A
     * subclass may alter this component sequence, if needed, but  this is
     * discouraged.
     * </li>
     * <li>
     * {@link #setupComponentsChain(LocalComponent[],String)} This method
     * invokes appropriate <code>setNext()</code> methods on input and filter
     * components, effectively binding components together in a chain for the
     * time of execution of a query.
     * </li>
     * <li>
     * {@link #beforeProcessingStartsHook(RequestContext context,
     * LocalComponent[] )} An empty hook method that can be overriden to
     * perform component customization before the processing starts.
     * </li>
     * <li>
     * {@link #afterProcessingStartedHook(RequestContext context,
     * LocalComponent[])} An empty hook method invoked after the call to
     * {@link LocalComponent#startProcessing(RequestContext)} method, but
     * before a call to {@link LocalComponent#endProcessing()} method.
     * </li>
     * <li>
     * {@link #afterProcessingEndedHook(RequestContext context,
     * LocalComponent[])} An empty hook method invoked after the call to
     * {@link LocalComponent#endProcessing()} method.
     * </li>
     * </ul>
     * 
     *
     * @see LocalProcess#query(RequestContext, String)
     */
    public Object query(RequestContext context, String query)
        throws Exception {
        LocalComponent[] components = getComponentInstancesForRequest(context);
        setupComponentsChain(components, query);

        beforeProcessingStartsHook(context, components);

        try {
            components[0].startProcessing(context);
            afterProcessingStartedHook(context, components);
            components[0].endProcessing();
            afterProcessingEndedHook(context, components);

            Object result = ((LocalOutputComponent) components[components.length -
                1]).getResult();

            return result;
        } catch (Throwable p) {
            components[0].processingErrorOccurred();

            if (p instanceof Exception) {
                throw (Exception) p;
            }

            if (p instanceof Error) {
                throw (Error) p;
            }

            // This should be unreachable, but the compiler complains.
            throw new RuntimeException(p);
        } finally {
            components[0].flushResources();
        }
    }

    /**
     * An empty hook method invoked after the call to {@link
     * LocalComponent#endProcessing()} method.
     *
     * @param components An array of components taking part in query
     *        processing.
     * @param context The context of the current request.
     */
    protected void afterProcessingEndedHook(RequestContext context,
        LocalComponent[] components) {
    }

    /**
     * An empty hook method invoked after the call to {@link
     * LocalComponent#startProcessing(RequestContext)} method, but before a
     * call to {@link LocalComponent#endProcessing()} method.
     *
     * @param components An array of components taking part in query
     *        processing.
     * @param context The context of the current request.
     */
    protected void afterProcessingStartedHook(RequestContext context,
        LocalComponent[] components) {
    }

    /**
     * An empty hook method that can be overriden to perform component
     * customization before the processing starts.
     *
     * @param components An array of components taking part in query
     *        processing.
     * @param context The context of the current request.
     */
    protected void beforeProcessingStartsHook(RequestContext context,
        LocalComponent[] components) {
    }

    /**
     * This method invokes appropriate <code>setNext()</code> methods on input
     * and filter components, effectively binding components together in a
     * chain for the time of execution of a query.
     * 
     * <p>
     * This method is static and provides access from within package.
     * </p>
     *
     * @param components An array of components taking part in query
     *        processing.
     * @param query The query.
     */
    protected static void setupComponentsChain(LocalComponent[] components,
        String query) {
        ((LocalInputComponent) components[0]).setNext(components[1]);

        for (int i = 1; i < (components.length - 1); i++) {
            ((LocalFilterComponent) components[i]).setNext(components[i + 1]);
        }

        ((LocalInputComponent) components[0]).setQuery(query);
    }

    /**
     * This method returns an ordered array of components used for query
     * processing. A subclass may alter this component sequence, if needed,
     * but  this is discouraged.
     *
     * @param context A request context object for the query execution passed
     *        from the component container.
     *
     * @return Should return an ordered array of {@link LocalComponent}
     *         objects. The array should start with the input component, end
     *         with an output component and have optional filter components in
     *         between.
     *
     * @throws MissingComponentException If any of the component factory
     *         identifiers is unavailable from the request context.
     * @throws Exception In case of any other failure.
     */
    protected LocalComponent[] getComponentInstancesForRequest(
        RequestContext context) throws MissingComponentException, Exception {
        LocalComponent[] components = new LocalComponent[2 + filters.size()];

        components[0] = context.getComponentInstance(input);

        if (!(components[0] instanceof LocalInputComponent)) {
            throw new Exception("Component: " +
                components[0].getClass().getName() + " does not implement " +
                LocalInputComponent.class.getName());
        }

        components[components.length - 1] = context.getComponentInstance(output);

        if (!(components[components.length - 1] instanceof LocalOutputComponent)) {
            throw new Exception("Component: " + input + " does not implement " +
                LocalOutputComponent.class.getName());
        }

        for (int i = 0; i < filters.size(); i++) {
            components[i + 1] = context.getComponentInstance((String) filters.get(
                        i));
        }

        return components;
    }

    /**
     * @return Returns an array of component factory identifiers of the filter
     *         components.
     */
    protected ArrayList getFilters() {
        return filters;
    }

    /**
     * @return Returns an identifier of the input component factory.
     */
    protected String getInput() {
        return input;
    }

    /**
     * @return Returns an identifier of the output component factory.
     */
    protected String getOutput() {
        return output;
    }

	/**
	 * @see com.dawidweiss.carrot.core.local.LocalProcess#getName()
	 */
	public String getName() {
        return name;
	}

	/**
	 * @see com.dawidweiss.carrot.core.local.LocalProcess#getDescription()
	 */
	public String getDescription() {
		return description;
	}
    
	/**
	 * @param description The description to set.
	 */
	protected void setDescription(String description) {
		this.description = description;
	}
    
	/**
	 * @param name The name to set.
	 */
    protected void setName(String name) {
		this.name = name;
	}
}
