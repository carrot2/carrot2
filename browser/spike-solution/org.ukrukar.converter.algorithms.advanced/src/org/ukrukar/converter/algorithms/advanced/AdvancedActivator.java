package org.ukrukar.converter.algorithms.advanced;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class AdvancedActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.ukrukar.converter.algorithms.advanced";

	// The shared instance
	private static AdvancedActivator plugin;
	
	/**
	 * The constructor
	 */
	public AdvancedActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AdvancedActivator getDefault() {
		return plugin;
	}

}
