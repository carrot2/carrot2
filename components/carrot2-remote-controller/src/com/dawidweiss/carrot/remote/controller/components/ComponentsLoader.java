
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
package com.dawidweiss.carrot.remote.controller.components;


import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.controller.carrot2.xmlbinding.ComponentDescriptor;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.Service;


/**
 * Loads descriptions and stores references to input/ output/ filter components. This object can
 * also be used to write back modified information about components.
 */
public class ComponentsLoader
{
    private static final Logger log = Logger.getLogger(ComponentsLoader.class);
    private List inputs = new ArrayList();
    private List outputs = new ArrayList();
    private List filters = new ArrayList();

    /**
     * A hashmap to speed up lookups. Keys are component nameKey elements (which should be unique).
     */
    private HashMap componentsByNameKey = new HashMap();

    /**
     * Instantiates an empty ComponentsLoader object.
     */
    public ComponentsLoader()
    {
    }

    /**
     * Adds all components contained in .xml files from a certain directory.
     *
     * @return The number of components added.
     */
    public int addComponentsFromDirectory(File directory)
    {
        if (!directory.isDirectory())
        {
            throw new IllegalArgumentException(
                "Argument must be a directory: " + directory.getAbsolutePath()
            );
        }

        int count = 0;
        File [] files = directory.listFiles(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".xml");
                    }
                }
            );

        // attempt to load all files and instantiate components in them.
        for (int i = 0; i < files.length; i++)
        {
            try
            {
                Service service = Service.unmarshal(new FileReader(files[i]));

                for (Iterator j = service.getComponentDescriptors().iterator(); j.hasNext();)
                {
                    ComponentDescriptor c = (ComponentDescriptor) j.next();

                    if (addComponent(c))
                    {
                        count++;
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Problems with processing XML file: " + files[i], e);
            }
        }

        return count;
    }


    /**
     * Adds a component to the loader. Component type is automatically recognized.
     *
     * @return true if the component has been recognized and added, false otherwise.
     */
    public boolean addComponent(ComponentDescriptor c)
    {
        if (componentsByNameKey.containsKey(c.getId()))
        {
            // duplicated component. Just warn and do nothing.
            log.warn(
                "Attempting to add a duplicated component: " + c.getId() + "(" + c.getServiceURL()
                + "), existing component's URL: "
                + ((ComponentDescriptor) componentsByNameKey.get(c.getId())).getServiceURL()
            );

            return false;
        }

        switch (c.getType())
        {
            case ComponentDescriptor.INPUT_TYPE:
                log.debug("Added input component: " + c.getId());
                inputs.add(c);
                break;

            case ComponentDescriptor.OUTPUT_TYPE:
                log.debug("Added output component: " + c.getId());
                outputs.add(c);
                break;

            case ComponentDescriptor.FILTER_TYPE:
                log.debug("Added filter component: " + c.getId());
                filters.add(c);
                break;

            default:
                log.error("Unrecognized component type: " + c.getType() + " (" + c + ")");

                return false;
        }

        componentsByNameKey.put(c.getId(), c);

        return true;
    }


    /**
     * Finds a given component by its nameKey. The lookup is done on a hashmap and should be fairly
     * fast.
     *
     * @return Component or null, if it couldn't be found.
     */
    public ComponentDescriptor findComponent(String nameKey)
    {
        return (ComponentDescriptor) componentsByNameKey.get(nameKey);
    }


    /**
     * Returns a list of available input components.
     */
    public Iterator getInputComponents()
    {
        return inputs.iterator();
    }


    /**
     * Returns a list of available output components.
     */
    public Iterator getOutputComponents()
    {
        return outputs.iterator();
    }


    /**
     * Returns a list of available filter components.
     */
    public Iterator getFilterComponents()
    {
        return filters.iterator();
    }
}
