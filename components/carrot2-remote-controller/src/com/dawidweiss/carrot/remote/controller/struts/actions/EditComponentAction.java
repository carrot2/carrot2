
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
package com.dawidweiss.carrot.remote.controller.struts.actions;


import com.dawidweiss.carrot.remote.controller.*;
import com.dawidweiss.carrot.remote.controller.process.*;
import com.dawidweiss.carrot.remote.controller.struts.forms.*;
import com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor;
import org.apache.struts.action.*;
import javax.servlet.http.*;


/**
 * This action synchronizes component form and component in the storage.
 */
public class EditComponentAction
    extends Action
{
    public ActionForward perform(
        ActionMapping mapping, ActionForm form, HttpServletRequest request,
        HttpServletResponse response
    )
    {
        ComponentForm component = (ComponentForm) form;

        String compId = request.getParameter("componentId");

        if (compId == null)
        {
            // new component is added ?
            component.setNameKey("");
            component.setDefaultName("");
            component.setServiceURL("");
            component.setInformationURL(null);
            component.setConfigurationURL(null);
            component.setEditable(true);
        }
        else
        {
            ProcessingChainLoader chains = (ProcessingChainLoader) super.getServlet()
                                                                        .getServletContext()
                                                                        .getAttribute(
                    Carrot2InitServlet.CARROT_PROCESSINGCHAINS_LOADER
                );
            ComponentDescriptor comp = chains.getComponentLoader().findComponent(compId);

            if (comp == null)
            {
                throw new RuntimeException("Cannot find component with this id: " + compId);
            }

            // existing component is edited.
            component.setNameKey(comp.getId());
            component.setServiceURL(comp.getServiceURL());
            component.setInformationURL((comp.getInfoURL() == null) ? ""
                                                                    : comp.getInfoURL()
            );
            component.setConfigurationURL(
                (comp.getConfigurationURL() == null) ? ""
                                                     : comp.getConfigurationURL()
            );
            component.setEditable(false /* hard coded false as for now. */);
        }

        return mapping.findForward("edit");
    }
}
