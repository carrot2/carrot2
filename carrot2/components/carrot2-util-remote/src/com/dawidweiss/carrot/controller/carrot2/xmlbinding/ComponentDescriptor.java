package com.dawidweiss.carrot.controller.carrot2.xmlbinding;

import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * Component descriptor holder and deserializer.
 * 
 * @author Dawid Weiss
 */
public class ComponentDescriptor {
    public static final int INPUT_TYPE = 1;
    public static final int OUTPUT_TYPE = 2;
    public static final int FILTER_TYPE = 3;
    
    private final String id;
    private final int type;
    private final String serviceURL;
    private final String infoURL;
    private final String configurationURL;

    public ComponentDescriptor(String id, int type, String serviceURL, String infoURL, String configurationURL) {
        this.id = id;
        this.type = type;
        this.serviceURL = serviceURL;
        this.infoURL = infoURL;
        this.configurationURL = configurationURL;
    }

    public String getId() {
        return id;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public int getType() {
        return type;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public String getConfigurationURL() {
        return configurationURL;
    }

    public static ComponentDescriptor unmarshall(Element component) throws DocumentException {

        final String id = component.attributeValue("id");
        final String typeString = component.attributeValue("type");
        final int type;
        if ("input".equals(typeString)) {
            type = INPUT_TYPE;
        } else if ("output".equals(typeString)) {
            type = OUTPUT_TYPE;
        } else if ("filter".equals(typeString)) {
            type = FILTER_TYPE;
        } else {
            throw new DocumentException("Unknown component type: " + typeString);
        }
        final String serviceURL = component.attributeValue("serviceURL");
        final String infoURL = component.attributeValue("infoURL");
        final String configurationURL = component.attributeValue("configurationURL");

        return new ComponentDescriptor(id, type, serviceURL, infoURL, configurationURL);
    }
}
