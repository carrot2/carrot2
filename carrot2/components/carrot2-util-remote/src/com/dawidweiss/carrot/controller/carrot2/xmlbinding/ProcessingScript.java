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
