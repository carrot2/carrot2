/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */

package com.dawidweiss.carrot.ant;


import org.apache.tools.ant.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.xml.parsers.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * An ANT task that parses a dependency file and extracts information about all depending objects
 * into a string.
 */
public class GetDependenciesTask
    extends org.apache.tools.ant.Task
{
    /** The component info file of the component for which dependencies are to  be retrieved. */
    private File componentDescriptor;

    /**
     * A list of pointers to dependency files for scanning against named dependencies (FileSet
     * objects)
     */
    private LinkedList dependencies = new LinkedList();

    /** The name of the property to save the dependent list of objects to. */
    private String propertyName;

    /** The reference id of a file list to create (can be null). */
    private String fileListId;
    
    /** Dumps a verbose info about dependencies. */
    private boolean verbose;

    /**
     * Public empty constructor
     */
    public GetDependenciesTask()
    {
    }

    /**
     * Sets a path to the dependency of the component for which dependencies are to be retrieved.
     */
    public void setComponentDescriptor(File dependencyFile)
        throws ParserConfigurationException, SAXException, IOException
    {
        this.componentDescriptor = dependencyFile;
    }


    /**
     * Crates a new fileset with a set of dependency files to scan.
     */
    public FileSet createDependencies()
    {
        FileSet newFileset = new FileSet();
        dependencies.add(newFileset);

        return newFileset;
    }


    /**
     * Setter for property attribute.
     */
    public void setProperty(String property)
    {
        this.propertyName = property;
    }


    /**
     * Sets the reference id of a file list to create.
     */
    public void setFileListId(String id)
    {
        this.fileListId = id;
    }

    /**
     * If true, verbose info is printed during dependency tracking.
     */
    public void setVerbose( boolean b) {
        this.verbose = b;
    }

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute()
        throws BuildException
    {
        if (getProject() == null)
        {
            throw new BuildException("Project reference is required.");
        }

        if (this.componentDescriptor == null)
        {
            throw new BuildException("dependency-file attribute is required.");
        }

        
        FileUtils futils = FileUtils.newFileUtils();

        try
        {
            ComponentDependency component = new ComponentDependency(this.componentDescriptor);

            // load all dependencies pointed to by embedded filesets.
            HashMap components = new HashMap();

            for (Iterator i = dependencies.iterator(); i.hasNext();)
            {
                FileSet fs = (FileSet) i.next();
                DirectoryScanner ds = fs.getDirectoryScanner(getProject());
                File fromDir = fs.getDir(getProject());
                String [] srcFiles = ds.getIncludedFiles();

                for (int j = 0; j < srcFiles.length; j++)
                {
                    ComponentDependency dep = new ComponentDependency(
                            futils.resolveFile(fromDir, srcFiles[j]).getCanonicalFile()
                        );

                    if (components.containsKey(dep.getName()))
                    {
                        throw new BuildException(
                            "Component name duplicated: " + dep.getFile() + " and "
                            + ((ComponentDependency) components.get(dep.getName())).getFile()
                        );
                    }

                    components.put(dep.getName(), dep);
                }
            }

            // gather dependencies required by the component.
            HashSet required = new HashSet();
            HashSet requiredObjects = new HashSet();
            ArrayList dependencyStack = new ArrayList();
            dependencyStack.addAll(component.getDependentComponentNames());
            int indent = 0;

            while (dependencyStack.isEmpty() == false)
            {
                Object object = dependencyStack.remove(
                    dependencyStack.size() - 1
                );
                
                if (object instanceof Integer) {
                    indent--;
                    continue;
                }

                String requiredComponent = (String) object; 

                if (required.contains(requiredComponent))
                {
                    if (verbose) {
                        verboseLog(indent, "[component, repeated above] " + requiredComponent);
                    }
                    continue;
                }

                if (!components.containsKey(requiredComponent))
                {
                    if (verbose) {
                        verboseLog(indent, "[component] ERROR NOT RESOLVED: " + requiredComponent);
                    }

                    throw new BuildException(
                        "Component required by '" + component.getName() + "' was not found: "
                        + requiredComponent
                    );
                }
                else
                {
                    required.add(requiredComponent);
                    if (verbose) {
                        verboseLog(indent, "[component] " + requiredComponent);
                    }

                    ComponentDependency requiredComponentDependency = (ComponentDependency) components
                        .get(requiredComponent);
                    
                    indent ++;
                    dependencyStack.add(new Integer(indent) );
                    dependencyStack.addAll(
                        requiredComponentDependency.getDependentComponentNames()
                    );
                    requiredObjects.addAll(requiredComponentDependency.getRequiredObjects());
                    requiredObjects.addAll(requiredComponentDependency.getProvidedObjects());
                    if (verbose) {
                        Collection c = requiredComponentDependency.getRequiredObjects();
                        for (Iterator k = c.iterator(); k.hasNext(); ) {
                            verboseLog(indent+1, "[req file] " + k.next()); 
                        }
                        c = requiredComponentDependency.getProvidedObjects();
                        for (Iterator k = c.iterator(); k.hasNext(); ) {
                            verboseLog(indent+1, "[own file] " + k.next()); 
                        }
                    }
                }
            }
            
            // add your own objects.
            requiredObjects.addAll(component.getRequiredObjects());
            if (verbose) {
                Collection c = component.getRequiredObjects();
                for (Iterator k = c.iterator(); k.hasNext(); ) {
                    verboseLog(0, "[file] " + k.next()); 
                }
            }

            StringBuffer componentsBuffer = new StringBuffer();

            for (Iterator i = required.iterator(); i.hasNext();)
            {
                componentsBuffer.append(i.next());

                if (i.hasNext())
                {
                    componentsBuffer.append(", ");
                }
            }

            Project project = getProject();

            if (!project.getDataTypeDefinitions().containsKey("AbsolutePathFileList"))
            {
                project.addDataTypeDefinition("AbsolutePathFileList", AbsolutePathFileList.class);
            }

            AbsolutePathFileList list = (AbsolutePathFileList) project.createDataType(
                    "AbsolutePathFileList"
                );

            if (fileListId != null)
            {
                project.addReference(fileListId, list);
            }

            StringBuffer objectsBuffer = new StringBuffer();

            for (Iterator i = requiredObjects.iterator(); i.hasNext();)
            {
                File requiredObject = (File) i.next();
                objectsBuffer.append(requiredObject);
                list.addFile(requiredObject);

                if (i.hasNext())
                {
                    objectsBuffer.append(", ");
                }
            }

            log(
                "Component: \"" + component.getName()
                + "\" has the following required components: " + componentsBuffer,
                Project.MSG_VERBOSE
            );
            log(
                "Component: \"" + component.getName() + "\" has the following required objects: "
                + objectsBuffer, Project.MSG_VERBOSE
            );

            if (propertyName != null) {
                project.setProperty(project.replaceProperties(propertyName), objectsBuffer.toString());
            }
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
    }
    
    /**
     * Logs a message with indentation. 
     */
    private final void verboseLog(int indent, String msg) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i<indent; i++) buf.append("  ");
        buf.append(msg);
        log(buf.toString(), Project.MSG_INFO);
    }

    /**
     * A class that represents a component
     */
    private class ComponentDependency
    {
        /** Name of this component. */
        private String name;

        /** A list of objects this component is composed of (File objects) */
        private ArrayList objects = new ArrayList();

        /** A dependency list (by name, Strings) of this component. */
        private ArrayList dependencies = new ArrayList();
        
        /** A list of objects this component provides (File objects) */
        private ArrayList provides = new ArrayList();

        /** A file pointer to this component's xml */
        private File file;

        public ComponentDependency(File file)
            throws ParserConfigurationException, SAXException, IOException
        {
            if (file.exists() == false)
            {
                throw new IOException("Dependency file cannot be read: " + file.getAbsolutePath());
            }

            if (file.canRead() == false)
            {
                throw new IOException("Dependency file cannot be read: " + file.getAbsolutePath());
            }

            this.file = file;

            log("Parsing dependency file: " + file, Project.MSG_VERBOSE);

            // parse the dependency file.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            builder = factory.newDocumentBuilder();

            Document dependency = null;
            dependency = builder.parse(file);

            Element root = dependency.getDocumentElement();

            if (!"component".equals(root.getNodeName()))
            {
                throw new SAXException("Root element name should be: 'component'");
            }

            this.name = root.getAttribute("name");

            FileUtils futils = FileUtils.newFileUtils();

            // gather all files this component consists of
            NodeList files = root.getElementsByTagName("file");

            for (int i = 0; i < files.getLength(); i++)
            {
                Element fileNode = (Element) files.item(i);

                String path = fileNode.getAttribute("location");

                if ((path == null) || "".equals(path.trim()))
                {
                    throw new SAXException("Expected 'location' attribute on file node.");
                }

                File object = futils.resolveFile(file.getParentFile(), path)
                                       .getCanonicalFile();
                objects.add(object);
                log("Added component object: " + object, Project.MSG_DEBUG);
            }

            // gather all dependencies by name
            files = root.getElementsByTagName("dependency");

            for (int i = 0; i < files.getLength(); i++)
            {
                Element fileNode = (Element) files.item(i);

                String depName = fileNode.getAttribute("name");

                if ((depName == null) || "".equals(depName.trim()))
                {
                    throw new SAXException("Expected 'name' attribute on dependency node.");
                }

                dependencies.add(depName);
                log("Added dependency to component: " + depName, Project.MSG_DEBUG);
            }
            
            // gather all provided objects by name
            files = root.getElementsByTagName("provides");
            for (int i = 0; i < files.getLength(); i++)
            {
                Element fileNode = (Element) files.item(i);

                String depName = fileNode.getAttribute("file");

                if ((depName == null) || "".equals(depName.trim()))
                {
                    throw new SAXException("Expected 'file' attribute on provides node.");
                }

                File object = futils.resolveFile(file.getParentFile(), depName)
                                       .getCanonicalFile();
                provides.add(object);
                log("Added provided object to component: " + depName, Project.MSG_DEBUG);
            }
            
        }

        /**
         * @return Returns a list with names of dependent components.
         */
        public Collection getDependentComponentNames()
        {
            return dependencies;
        }


        /**
         * @return Returns a list of required files for this component (without dependencies).
         */
        public Collection getRequiredObjects()
        {
            return objects;
        }
        
        public Collection getProvidedObjects() 
        {
            return provides;
        }


        /**
         * Returns the name of the component.
         */
        public String getName()
        {
            return name;
        }


        /**
         * Returns the File pointer
         */
        public File getFile()
        {
            return file;
        }
    }
}
