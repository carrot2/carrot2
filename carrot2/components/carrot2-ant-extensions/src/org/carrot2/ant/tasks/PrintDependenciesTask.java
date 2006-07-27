
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

package org.carrot2.ant.tasks;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.deps.ComponentInProfile;



/**
 * An ANT task that prints all the
 * dependencies of a given component. 
 */
public class PrintDependenciesTask extends BaseDependencyPathTask {

    /**
     * A file to which the results will be saved or null.
     */
    private File outputFile;

    /**
     * Name of a property where the results will be stored or null. 
     */
    private String property;


	/**
     * The component info file of the component
     * for which dependencies are to be retrieved.
     */
    private File componentDescriptor;

    /** Profile for dependency traversal. */
    private String profile;

    /**
     * Set the profile for dependency traversal.
     * @param profile
     */
    public void setProfile(String profile) {
        if (profile != null && "".equals(profile.trim())) {
            profile = null;
        } else
        	this.profile = profile;
    }

    public void setFile(File file) {
        this.outputFile = file;
    }
    
    /**
     * Sets the name of a property where the result
     * will be stored.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Sets a path to the dependency of the component for which dependencies are to be retrieved.
     */
    public void setComponentDescriptor(File dependencyFile) {
        this.componentDescriptor = dependencyFile;
    }

    /**
     * Checks if all parameters are correct.
     * @throws BuildException
     */
    protected void checkParameters() 
        throws BuildException
    {
        if (getProject() == null) {
            throw new BuildException("Project reference is required.");
        }

        if (this.componentDescriptor == null) {
            throw new BuildException("componentDescriptor attribute is required.");
        }
        
        if (this.outputFile == null && this.property == null)
            throw new BuildException("Either file or property attribute must be specified.");
    }
    
    protected static void indent(StringBuffer buf, int indent) {
        for (int i=0; i<indent; i++) buf.append("    ");
    }

    /**
     * Searches for components that are not up to date and updates
     * them if possible.
     */
    public void execute() throws BuildException {
        checkParameters();

        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            final Map components = super.getAllComponents();
            Utils.addComponentToMap(component, components);

            ComponentInProfile [] dependencies = component
                .getAllRequiredComponentDependencies(components, profile);
            
            ComponentInProfile self = new ComponentInProfile(component, profile, false);
            // Locate yourself again to get access to all dependencies.
            for (int i = 0; i<dependencies.length; i++) {
                if (dependencies[i].equals(self)) {
                    self = dependencies[i];
                    break;
                }
            }

            StringBuffer buf = new StringBuffer();
            buf.append("Component '" + component.getDescription() + "'\n");
            buf.append("Component built in profile: " + (profile == null ? "(default)" : "'" + profile + "'") + "\n\n"); 

            // Dump dependencies linearily.
            buf.append("Dependencies (aphabetical, aggregated):\n\n");
			Arrays.sort(dependencies, new Comparator() {
                public int compare(Object o1, Object o2) {
                	ComponentInProfile d1 = (ComponentInProfile) o1;
                	ComponentInProfile d2 = (ComponentInProfile) o2;
                	return d1.component.getDescription().compareToIgnoreCase(
                            d2.component.getDescription());
                }
			});
            if (dependencies.length - 1 <= 0) {
                indent(buf, 1);
                buf.append("[no dependencies]\n");
            } else {
	            for (int i=0; i<dependencies.length; i++) {
	                final ComponentInProfile dependency = dependencies[i];
	                if (dependency.equals(self)) {
	                	continue;
	                }
	                indent(buf, 1);
	                buf.append(i+1);
	                buf.append(") ");

                	buf.append("[" + dependency.component.getName() + "]");
	                if (dependency.profile != null) {
	                	buf.append(" [in profile: '" + dependency.profile + "']");
	                }
                    if (dependency.noCopy) {
                        buf.append(" [not copied, compile-time dependency]");
                    }
	                buf.append(" ");
	                buf.append(dependency.component.getDescription());
	                buf.append("\n");
	            }
            }

            // Dump dependencies as a graph.
            buf.append("\nDependencies (graph):\n\n");
            List deps = self.getDependencies();
            if (deps.size() == 0) {
                indent(buf, 1);
                buf.append("[no dependencies]\n");
            } else {
                // Declare an inner class for recursion only.
                final class Dumper {
                    private ArrayList columns = new ArrayList();
                    private final static String LINE = "line";
                    private final static String ENDLINE = "endline";
                    private final static String NOLINE = "noline";
                    private final static String INLINE = "inline";

                    public void dump(StringBuffer buf, int indent, ComponentInProfile component) {
	                    while (indent > columns.size()) {
                            columns.add(NOLINE);
	                    }
                        dumpInternal(buf, indent, component);
                    }

                    private void dumpInternal(StringBuffer buf, int indent, ComponentInProfile component) {
                        String v;

                        for (int i=0; i<indent; i++) {
                            v = (String) columns.get(i);
                            if (v == NOLINE) {
                                buf.append("   ");
                            } else if (v == LINE) {
                                buf.append("|  ");
                            } else if (v == INLINE) {
                                buf.append("|--");
                            } else if (v == ENDLINE) {
                                buf.append("\\--");
                            }
                        }

                        buf.append(component.component.getName());
    	                if (component.profile != null) {
    	                	buf.append(" [in profile: '" + component.profile + "']"); 
    	                }
                        if (component.noCopy) {
                            buf.append(" [not copied, compile-time dependency]");
                            
                        }
    	                buf.append("\n");

	                    while (indent + 1 >= columns.size()) {
                            columns.add(NOLINE);
	                    }
	                    
	                    if (indent > 0) {
		                    v = (String) columns.get(indent-1);
		                    if (v == INLINE) columns.set(indent-1, LINE);
		                    else if (v == ENDLINE) columns.set(indent-1, NOLINE);
	                    }

                        if (component.noCopy == false) {
        	                for (Iterator i = component.getDependencies().iterator(); i.hasNext();) {
        	                    ComponentInProfile cip = (ComponentInProfile) i.next();
        	                    columns.set(indent, i.hasNext() ? INLINE : ENDLINE);
        	                    dumpInternal(buf, indent+1, cip);
        	                }
                        }
                    }
                }

                Dumper dumper = new Dumper();
                dumper.dump(buf, 1, self);
            }

            if (this.outputFile != null) {
                Writer os = 
                    new OutputStreamWriter(new FileOutputStream(outputFile));
                try {
                    os.write(buf.toString());
                } finally {
                    os.close();
                }
            }
            if (this.property != null) {
                getProject().setNewProperty(property, buf.toString());
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

}
