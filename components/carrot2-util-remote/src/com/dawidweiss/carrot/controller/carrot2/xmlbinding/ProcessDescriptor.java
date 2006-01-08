
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

package com.dawidweiss.carrot.controller.carrot2.xmlbinding;

import java.io.Reader;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.SAXReader;

public class ProcessDescriptor {
    private String id;
    private String description;
    private Boolean hiddenValue;
    private ProcessingScript processingScript;
    
    public ProcessDescriptor() {
    }
    
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public ProcessingScript getProcessingScript() {
        return processingScript;
    }

    public boolean getHidden() {
        if (hiddenValue == null) {
            throw new IllegalStateException("Attribute hidden is not set.");
        }
        return hiddenValue.booleanValue();
    }

    public boolean hasHidden() {
        return hiddenValue != null;
    }
    
    public static ProcessDescriptor unmarshal(Reader stream) throws DocumentException {
        final ProcessDescriptor descriptor = new ProcessDescriptor();

        final SAXReader reader = new SAXReader();
        final Element root = reader.read(stream).getRootElement();

        if (!"process".equals(root.getName())) {
            throw new DocumentException("Root name should be 'process': " + root.getName());
        }
        
        descriptor.id = root.attributeValue("id");
        descriptor.description = root.attributeValue("description");
        Attribute attribute = root.attribute("hidden");
        if (attribute != null) {
            descriptor.hiddenValue = new Boolean(attribute.getValue());
        } else {
            descriptor.hiddenValue = null;
        }
        
        final Element pScript = root.element("processing-script");
        descriptor.processingScript = new ProcessingScript(
                pScript.getText(), pScript.attributeValue("language"));
        
        return descriptor;
    }
}