
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.local.controller;

import java.util.List;
import java.util.Map;

import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.core.local.ProcessingResult;


/**
 * @deprecated Deprecated in favor of
 * {@link com.dawidweiss.carrot.core.local.LocalControllerBase}.
 */
public final class LocalController extends LocalControllerBase {
    public void addComponentFactory(String componentId, LocalComponentFactory factory, int i) {
        super.addLocalComponentFactory(componentId, factory);
    }

    public ProcessingResult query(String processId, String query, Map requestParameters) throws MissingProcessException, Exception {
        return super.query(processId, query, new FallbackMap(requestParameters));
    }
    
    public String[] getProcessNames() {
        final List processIds = super.getProcessIds();
        return (String []) processIds.toArray(new String[processIds.size()]);
    }    
}
