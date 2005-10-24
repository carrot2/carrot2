package carrot2.demo;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dawidweiss.carrot.core.local.DuplicatedKeyException;
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.loaders.ComponentInitializationException;

/**
 * A Carrot2 clustering demo context.
 * 
 * @author Dawid Weiss
 */
public class DemoContext {

    /** Local Carrot2 controller */
    private LocalController controller = new LocalControllerBase();

    /** Maps process identifiers to their user interface names. */
    private HashMap processIdToName;
    
    /**
     * Initialize the demo context, create local controller 
     * and component factories.
     */
    public void initialize() {
        final File componentsDir = new File("components");
        if (componentsDir.isDirectory() == false) {
            throw new RuntimeException("Components directory not found: "
                    + componentsDir.getAbsolutePath());
        }
        final File processesDir = new File("processes");
        if (processesDir.isDirectory() == false) {
            throw new RuntimeException("Components directory not found: "
                    + componentsDir.getAbsolutePath());
        }

        final ControllerHelper cl = new ControllerHelper();

        //
        // Add scripted/ custom components and processes
        //
        try {
            cl.addComponentFactoriesFromDirectory(controller, componentsDir);
            cl.addProcessesFromDirectory(controller, processesDir);
        } catch (DuplicatedKeyException e) {
            throw new RuntimeException("Identifiers of components and processes must be unique.", e);
        } catch (ComponentInitializationException e) {
            throw new RuntimeException("Cannot initialize component.", e);
        } catch (Exception e) {
            throw new RuntimeException("Unhandled exception when initializing components and processes.", e);
        }

        final HashMap processIdToName = new HashMap();
        final List processIds = controller.getProcessIds();
        for (final Iterator i = processIds.iterator(); i.hasNext();) {
            try {
                final String processId = (String) i.next();
                String processName = controller.getProcessName(processId);
                if (processName == null || "".equals(processName)) {
                    processName = processId;
                }
                processIdToName.put(processId, processName);
            } catch (MissingProcessException e) {
                throw new Error("Process identifier not associated with any name?", e);
            }
        }
        this.processIdToName = processIdToName;
    }

    /**
     * Returns a map of process identifier - process name.
     */
    public Map getProcessIdToProcessNameMap() {
        return processIdToName;
    }

    /**
     * Returns process settings for the given process or <code>null</code> if no
     * settings are present for this process.
     */
    public ProcessSettings getSettingsObject(final String processId) {
        return new EmptyProcessSettings();
    }

    /**
     * Returns the process controller preconfigured in the context.
     */
    public LocalController getController() {
        return this.controller;
    }
}
