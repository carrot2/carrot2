package com.dawidweiss.carrot.remote.controller.util;


import java.lang.reflect.Modifier;


public class InterfaceMapping
    implements PostConfigureCheck
{
    private Class  interfaceClass;
    private String elementName;
    

    public InterfaceMapping()
    {
    }
    
    public InterfaceMapping(String elementName, String interfaceName)
        throws ClassNotFoundException
    {
        setInterfaceName( interfaceName );
        setElementName( elementName );
    }


    public void setInterfaceName(String interfaceName)
        throws ClassNotFoundException
    {
        this.interfaceClass = Thread.currentThread().getContextClassLoader().loadClass(interfaceName);
        if (interfaceClass.isInterface()==false || Modifier.isPublic(interfaceClass.getModifiers())==false)
            throw new IllegalArgumentException("Class " + interfaceClass.getName()
                + " is" + " not an interface or is not public.");
    }

    public String getInterfaceName()
    {
        return interfaceClass.getName();
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }
    
    public String getElementName()
    {
        return this.elementName;
    }

    public String assertConfigured()
    {
        if (interfaceClass == null)
            return "Interface class is required.";
        if (elementName == null || "".equals(elementName.trim()))
            return "Element name is required and cannot be an empty string.";
        return null;
    }
    
    public Class getInterfaceClass()
    {
        return this.interfaceClass;
    }
}
