
package com.dawidweiss.carrot.ant;

import org.apache.tools.ant.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.deps.ComponentDependencyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.LinkedList;


/**
 * An ANT task that prints all the
 * dependencies of a given component. 
 */
public class PrintDependencies extends Task {

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

    /**
     * A list of <code>FileSet</code> objects
     * that point to component dependency descriptors.
     * This list is used when searching for named
     * dependencies. 
     */
    private LinkedList dependencies = new LinkedList();

    /** Profile for dependency traversal. */
    private String profile;
    
    /**
     * Public empty constructor
     */
    public PrintDependencies() {
    }

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
     * Crates a new fileset with a set of dependency files to scan.
     */
    public FileSet createDependencies() {
        FileSet newFileset = new FileSet();
        dependencies.add(newFileset);
        return newFileset;
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

    /**
     * Searches for components that are not up to date and updates
     * them if possible.
     */
    public void execute() throws BuildException {
        
        checkParameters();
        
        FileUtils futils = FileUtils.newFileUtils();
        try {
            ComponentDependency component = 
                new ComponentDependency(getProject(), this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            HashMap components = new HashMap();
            components.put(component.getName(), component);
            ComponentDependencyUtils.loadComponentDependencies( futils, getProject(), this.dependencies, components);

            ComponentDependency [] dependencies = component
                .getAllRequiredComponentDependencies( components, profile );

            StringBuffer buf = new StringBuffer();
            buf.append("Component '" + component.getDescription() + "'\n\n");
            buf.append("Dependency profile: " + (profile == null ? "(default)" : "'" + profile + "'") + "\n"); 
            buf.append("Dependencies:\n");

			Arrays.sort(dependencies, new Comparator() {
                public int compare(Object o1, Object o2) {
                	ComponentDependency d1 = (ComponentDependency) o1;
                	ComponentDependency d2 = (ComponentDependency) o2;
                	return d1.getDescription().compareToIgnoreCase(d2.getDescription());
                }
			});

            for (int i=0;i<dependencies.length;i++) {
                ComponentDependency dependency = dependencies[i];
                
                if (dependency.getName().equals( component.getName() )) {
                	continue;
                }
                
                buf.append("   - " + dependency.getDescription());
                if (dependency.getActiveProfile() != null) {
                	buf.append(" [in profile: '" + dependency.getActiveProfile() + "']"); 
                }
                buf.append("\n");
            }
            if (dependencies.length - 1 <= 0) {
                buf.append("no dependencies.");
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
