
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

package com.dawidweiss.carrot.local.controller;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.DuplicatedKeyException;
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.local.controller.loaders.*;


/**
 * This class contains utility methods for helping with instantiation of {@link
 * com.dawidweiss.carrot.core.local.LocalComponentFactory} and {@link
 * com.dawidweiss.carrot.core.local.LocalProcess} objects  from their
 * descriptions in XML, BeanShell any other persistent form and adding them to
 * a controller component.
 * 
 * <p>
 * Component factories are loaded using {@link ComponentFactoryLoader} and
 * {@link ProcessLoader} objects. Every loader is associated with an
 * <i>extension</i>. This extension maps directly to file extensions,  or must
 * be explicitly stated if only data stream (<code>InputStream</code>) is
 * provided.
 * </p>
 * 
 * <p>
 * A set of loaders is automatically added to objects of this class. These
 * defaults can be erased using {@link #clearLoaders()} method.
 * </p>
 * 
 * <p>
 * Please note that component and process extensions may be identical. This may
 * cause conflicts when using {@link
 * #addComponentFactoriesFromDirectory(LocalController controller, File
 * directory)} method that attempts to automatically load all components based
 * on file extensions. Split loader and process definitions into two
 * directories, or use a custom {@link FileFilter} to correctly choose between
 * processes and component factories.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class ControllerHelper {
    /**
     * Default extension of the XML-based component factory loader.
     */
    public static final String EXT_COMPONENT_FACTORY_LOADER_XML = "xml";

    /**
     * Default extension of the BeanShell-based component factory loader.
     */
    public static final String EXT_COMPONENT_FACTORY_LOADER_BEANSHELL = "bsh";

    /**
     * Default extension of the XML-based process loader.
     */
    public static final String EXT_PROCESS_LOADER_XML = "xml";

    /**
     * Default extension of the BeanShell-based process loader.
     */
    public static final String EXT_PROCESS_LOADER_BSH = "bsh";

    /**
     * A map of available component factory definition loaders.
     */
    private HashMap componentFactoryLoaders = new HashMap();

    /**
     * A map of available process definition loaders.
     */
    private HashMap processLoaders = new HashMap();

    /**
     * Instantiates a new component loader utility class and adds the default
     * loaders to it.
     */
    public ControllerHelper() {
        try {
            addComponentFactoryLoader(EXT_COMPONENT_FACTORY_LOADER_XML,
                new XmlFactoryDescriptionLoader());
            addComponentFactoryLoader(EXT_COMPONENT_FACTORY_LOADER_BEANSHELL,
                new BeanShellFactoryDescriptionLoader());
            addProcessLoader(EXT_PROCESS_LOADER_XML, new XmlProcessLoader());
            addProcessLoader(EXT_PROCESS_LOADER_BSH, new BeanShellProcessLoader());
        } catch (DuplicatedKeyException e) {
            // impossible state: we know what we're adding.
        }
    }

    /**
     * Clears any loader extension mappings. Use this method if you need to
     * change the default loader  extensions.
     */
    public void clearLoaders() {
        this.componentFactoryLoaders.clear();
        this.processLoaders.clear();
    }

    /**
     * Maps a {@link ComponentFactoryLoader} to the given extension.
     *
     * @param loaderExtension The extension to map the loader to.
     * @param loader The loader to use.
     *
     * @throws DuplicatedKeyException If the extension is already mapped to a
     *         loader, this exception is thrown.
     */
    public void addComponentFactoryLoader(String loaderExtension,
        ComponentFactoryLoader loader) throws DuplicatedKeyException {
        if (componentFactoryLoaders.containsKey(loaderExtension)) {
            throw new DuplicatedKeyException(
                "Loader for this extension exists: " + loaderExtension);
        }

        componentFactoryLoaders.put(loaderExtension, loader);
    }
    
    /**
     * Returns a component factory loader with a given extension or
     * <code>null</code> if no such extension is available.
     */
    public ComponentFactoryLoader getComponentFactoryLoader(String loaderExtension) {
        return (ComponentFactoryLoader) this.componentFactoryLoaders.get(loaderExtension);
    }

    /**
     * Maps a {@link ProcessLoader} to the given extension.
     *
     * @param loaderExtension The extension to map the loader to.
     * @param loader The loader to use.
     *
     * @throws DuplicatedKeyException If the extension is already mapped to a
     *         loader, this exception is thrown.
     */
    public void addProcessLoader(String loaderExtension, ProcessLoader loader)
        throws DuplicatedKeyException {
        if (processLoaders.containsKey(loaderExtension)) {
            throw new DuplicatedKeyException(
                "Loader for this extension exists: " + loaderExtension);
        }

        processLoaders.put(loaderExtension, loader);
    }
    
    /**
     * Returns a registered process loader or <code>null</code>
     * if no such loader exists.
     */
    public ProcessLoader getProcessLoader(String loaderExtension) {
        return (ProcessLoader) processLoaders.get(loaderExtension);
    }

    /**
     * Creates a local process definition
     * read from a data stream using a process loader matching the
     * provided file extension.
     * 
     * <p>
     * The stream is always closed when this method returns.
     * </p>
     *
     * @param loaderExtension The file extension of a loader to use.
     * @param data Data stream.
     * @return Returns the loaded process definition.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws Exception Thrown if the process has been loaded and initialized,
     *         but adding it to a controller failed.
     */
    public LoadedProcess loadProcess(final String loaderExtension, final InputStream data)
        throws LoaderExtensionUnknownException, IOException, 
            DuplicatedKeyException, Exception {
        try {
            if (!processLoaders.containsKey(loaderExtension)) {
                throw new LoaderExtensionUnknownException(
                    "Loader unknown for extension: " + loaderExtension);
            }

            final ProcessLoader cl = (ProcessLoader) processLoaders.get(loaderExtension);
            final LoadedProcess loaded = cl.load(data);
            return loaded;
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                // Ignore closing exception.
            }
        }
    }

    /**
     * Creates a local process definition read from a file.
     * The extension of the file is used to find the appropriate loader.
     *
     * @param file The file to load the process from.
     * @return Returns the loaded process definition.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws FileNotFoundException Thrown if the file does not exist.
     * @throws Exception Thrown if the process has been loaded and initialized,
     *         but adding it to a controller failed.
     */
    public LoadedProcess loadProcess(File file)
        throws FileNotFoundException, LoaderExtensionUnknownException, 
            IOException, DuplicatedKeyException, Exception {
        String extension = getExtension(file);
        return loadProcess(extension, new FileInputStream(file));
    }

    /**
     * Loads all processes in a directory and returns an array of {@link LoadedProcess}.
     * <b>Only recognized loaders</b> (file extensions) will trigger component
     * load attempt.
     */
    public LoadedProcess [] loadProcessesFromDirectory(File directory)
        throws IOException, DuplicatedKeyException, Exception {
        return loadProcessesFromDirectory(directory, getProcessFileFilter());
    }

    /**
     * Returns a file filter that accepts all files with extensions matching processes.
     */
    public FileFilter getProcessFileFilter() {
        final FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return processLoaders.containsKey(getExtension(pathname));
            }
        };
        return filter;
    }
    
    /**
     * Returns a file filter that accepts all files with extensions matching
     * components.
     */
    public FileFilter getComponentFilter() {
        final FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return componentFactoryLoaders.containsKey(getExtension(
                        pathname));
            }
        };
        return filter;
    }

    /**
     * Loads processes from a directory and returns an array of {@link LoadedProcess}.
     *
     * @param directory The directory to scan. Subdirectories are not
     *        traversed.
     *
     * @return Returns a list of {@link LoadedProcess} objects.
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws Exception Thrown if some process has been loaded and
     *         initialized, but adding it to a controller failed.
     */
    public LoadedProcess [] loadProcessesFromDirectory(File directory, FileFilter filter)
        throws IOException, DuplicatedKeyException, Exception {
        final File[] files = directory.listFiles(filter);

        final ArrayList loadedProcesses = new ArrayList();
        for (int i = 0; i < files.length; i++) {
            try {
                final LoadedProcess loadedProcess = loadProcess(files[i]);
                loadedProcesses.add(loadedProcess);
            } catch (FileNotFoundException e) {
                // file has been apparently deleted between list()
                // and its access time. ok, ignore it.
            }
        }
        return (LoadedProcess []) loadedProcesses.toArray(
                new LoadedProcess [loadedProcesses.size()]);
    }

    /**
     * Loads all processes in a directory and adds them to a given controller.
     * <b>Only recognized loaders</b> (file extensions) will trigger component
     * load attempt.
     *
     * @param controller The controller to add the process to.
     * @param directory The directory to scan. Subdirectories are not
     *        traversed.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws Exception Thrown if some process has been loaded and
     *         initialized, but adding it to a controller failed.
     */
    public void addProcessesFromDirectory(LocalController controller, File directory)
        throws IOException, DuplicatedKeyException, Exception {
        final LoadedProcess [] loadedProcesses = loadProcessesFromDirectory(directory);
        for (int i = 0; i < loadedProcesses.length; i++) {
            final LoadedProcess lp = loadedProcesses[i];
            controller.addProcess(lp.getId(), lp.getProcess());
        }
    }

    /**
     * Applies {@link #loadProcess(File file)}
     * method to all files in the directory that return <code>true</code> from
     * the provided file name selector filter and adds processes to the given
     * controller.
     *
     * @param controller The controller to add the process to.
     * @param directory The directory to scan. Subdirectories are not
     *        traversed.
     * @param filter FileFilter object for selecting files to add to the
     *        controller.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     * @throws Exception Thrown if some process has been loaded and
     *         initialized, but adding it to a controller failed.
     */
    public void addProcessesFromDirectory(LocalController controller,
        File directory, FileFilter filter)
        throws LoaderExtensionUnknownException, IOException, 
            DuplicatedKeyException, Exception {
        File[] files = directory.listFiles(filter);

        for (int i = 0; i < files.length; i++) {
            try {
                final LoadedProcess loadedProcess = loadProcess(files[i]);
                controller.addProcess(loadedProcess.getId(), loadedProcess.getProcess());
            } catch (FileNotFoundException e) {
                // file has been apparently deleted between list()
                // and its access time. ok, ignore it.
            }
        }
    }

    /**
     * Returns a {@link LoadedComponentFactory} loaded with a loader matching <code>loaderExtension</code>
     * and instantiated from a given input stream. 
     */
    public LoadedComponentFactory loadComponentFactory(String loaderExtension, InputStream data) 
        throws LoaderExtensionUnknownException, IOException, ComponentInitializationException
    {
        if (!componentFactoryLoaders.containsKey(loaderExtension)) {
            throw new LoaderExtensionUnknownException(
                "Loader unknown for extension: " + loaderExtension);
        }

        final ComponentFactoryLoader cl = (ComponentFactoryLoader) componentFactoryLoaders.get(loaderExtension);
        return cl.load(data);
    }

    /**
     * Loads a component factory from a file.
     */
    public LoadedComponentFactory loadComponentFactory(File file) 
        throws LoaderExtensionUnknownException, IOException, ComponentInitializationException 
    {
        final InputStream is = new FileInputStream(file);
        try {
            return loadComponentFactory(getExtension(file), is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore.
            }
        }
    }
    
    /**
     * Adds a component factory to the local controller, the factory is read
     * from a data stream using a loader matching the provided extension.
     * 
     * <p>
     * The stream is always closed when this method returns.
     * </p>
     *
     * @param controller The controller to add the factory to.
     * @param loaderExtension The extension of a loader to use.
     * @param data Data stream.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     */
    public void addComponentFactory(LocalController controller,
            String loaderExtension, InputStream data)
        throws IOException, LoaderExtensionUnknownException, 
        	ComponentInitializationException, DuplicatedKeyException {
        try {
            final LoadedComponentFactory loaded = loadComponentFactory(loaderExtension, data);
            controller.addLocalComponentFactory(loaded.getId(), loaded.getFactory());
        } finally {
            try {
                data.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Adds a component factory to the local controller, the factory is read
     * from a file. The extension of the file is used to find the appropriate
     * loader.
     *
     * @param controller The controller to add the factory to.
     * @param file The file to load the factory from.
     *
     * @throws FileNotFoundException Thrown if the file does not exist.
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     * @throws ComponentInitializationException Thrown if factory has been loaded, but
     *         components instantiation failed in the controller.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     */
    public void addComponentFactory(LocalController controller, File file)
        throws FileNotFoundException, IOException, 
            LoaderExtensionUnknownException, ComponentInitializationException, 
            DuplicatedKeyException {
        addComponentFactory(controller, getExtension(file), new FileInputStream(file));
    }

    /**
     * Loads component factories from a given directory. 
     * @throws ComponentInitializationException 
     * @throws IOException 
     */
    public LoadedComponentFactory [] loadComponentFactoriesFromDirectory(File directory) 
        throws IOException, ComponentInitializationException
    {
        final File[] files = directory.listFiles(getComponentFilter());
        final ArrayList list = new ArrayList(files.length);
        for (int i = 0; i < files.length; i++) {
            try {
                list.add(loadComponentFactory(files[i]));
            } catch (FileNotFoundException e) {
                // file has been apparently deleted between list()
                // and its access time. ok, ignore it.
            } catch (LoaderExtensionUnknownException e) {
                // This is impossible, because we checked
                // that the loader knows the extension of this file.
                throw new RuntimeException("Impossible state reached.");
            }
        }
        return (LoadedComponentFactory[]) list.toArray(
                new LoadedComponentFactory[list.size()]);
    }

    /**
     * Applies {@link #addComponentFactory(LocalController controller, File
     * file)} method to all files in the directory. <b>Only recognized
     * loaders</b> (file extensions) will trigger component load attempt.
     *
     * @param controller The controller to add the factory to.
     * @param directory The directory to load components from. Subdirectories
     *        are not traversed.
     *        
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws ComponentInitializationException Thrown if factory has been loaded, but
     *         components instantiation failed in the controller.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     */
    public void addComponentFactoriesFromDirectory(LocalController controller, File directory)
        throws IOException, ComponentInitializationException, DuplicatedKeyException {
        final LoadedComponentFactory [] loaded = loadComponentFactoriesFromDirectory(directory);
        for (int i = 0; i < loaded.length; i++) {
            controller.addLocalComponentFactory(loaded[i].getId(), loaded[i].getFactory());
        }
    }

    /**
     * Applies {@link #addComponentFactory(LocalController controller, File
     * file)} method to all files in the directory that return
     * <code>true</code> from the provided file name selector filter.
     *
     * @param controller The controller to add the factory to.
     * @param directory The directory to load components from. Subdirectories
     *        are not traversed.
     * @param filter Custom file name filter to use.
     *
     * @throws IOException Thrown if an i/o exception occurs.
     * @throws ComponentInitializationException Thrown if factory has been loaded, but
     *         components instantiation failed in the controller.
     * @throws DuplicatedKeyException Thrown if the controller already has a
     *         component factory mapped to the identifier of the newly loaded
     *         factory.
     * @throws LoaderExtensionUnknownException Thrown if there is no loader
     *         associated with the provided extension.
     */
    public void addComponentFactoriesFromDirectory(LocalController controller,
        File directory, FileFilter filter)
        throws IOException, LoaderExtensionUnknownException, 
        	ComponentInitializationException, DuplicatedKeyException {
        File[] files = directory.listFiles(filter);

        for (int i = 0; i < files.length; i++) {
            try {
                addComponentFactory(controller, files[i]);
            } catch (FileNotFoundException e) {
                // file has been apparently deleted between list()
                // and its access time. ok, ignore it.
            }
        }
    }

    /**
     * @param file A {@link File} object to return the file name extension
     *        from.
     *
     * @return Returns the file name extension, or an empty string if no
     *         extension is available.
     */
    protected final String getExtension(File file) {
        String fileName = file.getName();

        int lastDotIndex = fileName.lastIndexOf('.');

        if ((lastDotIndex == -1) || ((lastDotIndex + 1) == fileName.length())) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }
}
