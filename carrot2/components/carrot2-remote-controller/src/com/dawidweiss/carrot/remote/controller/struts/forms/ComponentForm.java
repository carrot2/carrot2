

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.remote.controller.struts.forms;


import org.apache.struts.action.*;
import java.net.*;
import javax.servlet.http.*;


/**
 * Describes component properties.
 */
public class ComponentForm
    extends ActionForm
{
    private String defaultName;
    private String nameKey;
    private String serviceURL;
    private String informationURL;
    private String configurationURL;
    private boolean editable;

    // --------------------------------------------------------------- accessors
    public boolean isEditable()
    {
        return editable;
    }


    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }


    public String getDefaultName()
    {
        return defaultName;
    }


    public void setDefaultName(String defaultName)
    {
        this.defaultName = defaultName;
    }


    public String getNameKey()
    {
        return nameKey;
    }


    public void setNameKey(String nameKey)
    {
        this.nameKey = nameKey;
    }


    public String getServiceURL()
    {
        return serviceURL;
    }


    public void setServiceURL(String serviceURL)
    {
        this.serviceURL = serviceURL;
    }


    public String getInformationURL()
    {
        return informationURL;
    }


    public void setInformationURL(String informationURL)
    {
        this.informationURL = informationURL;
    }


    public String getConfigurationURL()
    {
        return configurationURL;
    }


    public void setConfigurationURL(String configurationURL)
    {
        this.configurationURL = configurationURL;
    }


    // ---------------------------------------------------------- initialization

    /**
     * Validates the form.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
        ActionErrors errors = new ActionErrors();

        if (isEmpty(defaultName))
        {
            errors.add(
                "defaultName", new ActionError("errors.forms.missing-field", "Default name")
            );
        }

        if (isEmpty(serviceURL))
        {
            errors.add("serviceURL", new ActionError("errors.forms.missing-field", "Service URL"));
        }

        try
        {
            new URL(serviceURL);
        }
        catch (MalformedURLException e)
        {
            errors.add(
                "serviceURL", new ActionError("errors.forms.incorrect-value", "Service URL")
            );
        }

        if (!isEmpty(informationURL))
        {
            try
            {
                new URL(informationURL);
            }
            catch (MalformedURLException e)
            {
                errors.add(
                    "informationURL",
                    new ActionError("errors.forms.incorrect-value", "Information URL")
                );
            }
        }

        if (!isEmpty(configurationURL))
        {
            try
            {
                new URL(configurationURL);
            }
            catch (MalformedURLException e)
            {
                errors.add(
                    "configurationURL",
                    new ActionError("errors.forms.incorrect-value", "Configuration URL")
                );
            }
        }

        if (errors.size() > 0)
        {
            return errors;
        }
        else
        {
            return null;
        }
    }


    /**
     * Detects whether the string is null or empty.
     */
    private boolean isEmpty(String property)
    {
        if ((property == null) || property.trim().equals(""))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
