
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

package com.dawidweiss.carrot.controller.carrot2.xmlbinding;

/**
 * Represents a processing script of a {@link com.dawidweiss.carrot.controller.carrot2.xmlbinding.ProcessDescriptor}.
 * 
 * @author Dawid Weiss
 */
public class ProcessingScript {
    
    private final String content;
    private final String language;

    ProcessingScript(String content, String language) {
        this.content = content;
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public String getLanguage() {
        return language;
    }
}
