
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
package com.dawidweiss.carrot.remote.controller.util;



import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/*
 * Already implemented:
 *
 * - dynamically instantiated mapped interfaces (class attribute)
 * - mapped classes
 * - factory methods
 * - properties
 *
 * Not yet implemented
 * - array setters
 */

public class Loader
{
    private boolean failOnUnrecognizedProperties;
    private boolean validation;
    private HashMap mappings;
    private Map     objectsRepository;


    public Loader()
    {
        this.mappings = new HashMap();
        this.objectsRepository = new HashMap();
    }


    public void setObjectsRepository(Map repository)
    {
        this.objectsRepository = new HashMap(repository);
    }

    public Map getObjectsRepository()
    {
        return new HashMap( this.objectsRepository );
    }


    public void setInterfaceMappings( InterfaceMapping [] mappings )
    {
        for (int i=0;i<mappings.length;i++)
        {
            if (this.mappings.put(mappings[i].getElementName(), mappings[i]) != null)
            {
                throw new RuntimeException("Element: "
                    + mappings[i].getElementName() + " cannot be mapped more than once.");
            }
        }
    }


    public void setClassMappings( ClassMapping [] mappings )
    {
        for (int i=0;i<mappings.length;i++)
        {
            if (this.mappings.put(mappings[i].getElementName(), mappings[i]) != null)
            {
                throw new RuntimeException("Element: "
                    + mappings[i].getElementName() + " cannot be mapped more than once.");
            }
        }
    }

    public void setValidation(boolean validation)
    {
        this.validation = validation;
    }


    public static Loader loadLoader(InputStream XMLStream)
        throws IOException, ClassNotFoundException
    {
        Loader l = new Loader();
        l.setFailOnUnrecognizedProperties(true);
        l.setClassMappings( new ClassMapping [] {
            new ClassMapping( "loader", Loader.class.getName() ),
            new ClassMapping( "class-mappings", ClassMapping.class.getName()),
            new ClassMapping( "interface-mappings", InterfaceMapping.class.getName())
        });

        return (Loader) l.load(XMLStream);
    }


    public Object load(InputStream XMLStream)
        throws IOException
    {
        Element root;
        try {
            root = new SAXReader(validation).read(XMLStream).getRootElement();
        } catch (DocumentException e) {
            throw new IOException("Can't load descriptor: " + e.toString());
        }
        return configureObject( root, null );
    }

    private final Object configureObject(Element node, Object parent)
        throws IOException
    {
        String nodeName = node.getName();
        Object mapping;
        Object object = null;

        // check if the node has 'refid' attribute. if yes, try the repository first.
        if (node.attribute("refid") != null)
        {
            if (!node.elements().isEmpty())
                throw new IOException("Referenced node " + nodeName
                    + " cannot be redefined or contain new attributes.");

            String refid = node.attributeValue("refid");
            if (!objectsRepository.containsKey(refid))
                throw new IOException("Referenced node " + nodeName + "with ID equal to "
                    + refid + " does not exist.");

            object = objectsRepository.get(refid);
            return object;
        }
        else
        if ((mapping=mappings.get(nodeName))!=null)
        {
            if (mapping instanceof InterfaceMapping)
            {
                InterfaceMapping imapping = (InterfaceMapping) mapping;

                // load interface instance from class attribute,
                if (node.attribute("class")==null)
                    throw new IOException("Missing class attribute in mapping for interface "
                        + imapping.getInterfaceName());

                String interfaceInstanceClassName =
                    node.attribute("class").getValue().trim();
                try
                {
                    Class interfaceInstance = Thread.currentThread().getContextClassLoader().loadClass(
                        interfaceInstanceClassName);
                    if ( Arrays.asList(interfaceInstance.getInterfaces()).contains(imapping.getInterfaceClass())==false)
                        throw new IOException("Class " + interfaceInstance
                            + " does not implement interface " + imapping.getInterfaceName());
                    object = interfaceInstance.newInstance();
                }
                catch (Exception e)
                {
                    throw new IOException(e.toString());
                }
            }
            else if (mapping instanceof ClassMapping)
            {
                // load class instance
                ClassMapping cmapping = (ClassMapping) mapping;
                try
                {
                    object = cmapping.getNewInstance();
                }
                catch (Exception e)
                {
                    throw new IOException(e.toString());
                }
            }
            else
            throw new RuntimeException("Unknown element mapping class: " + mapping.getClass().getName());
        }
        else
        {
            // check if parent has 'createXXX' method. if yes, create
            // the object using such factory-like method.
            Method method = MethodUtils.getAccessibleMethod(parent.getClass(),
                            "create" + toJavaNameConvention(node.getName(), true),
                            new Class [0]);
            if (method != null)
            {
                if (method.getReturnType() != null)
                {
                    try
                    {
                        object = method.invoke(parent, new Object [0]);
                    }
                    catch (InvocationTargetException e)
                    {
                        throw new IOException("Exception when creating a new object using factory method: "
                            + "create" + toJavaNameConvention(node.getName(), true) + " on object of class "
                            + parent.getClass().getName() + ": " + e.getTargetException().toString());
                    }
                    catch (Exception e)
                    {
                        throw new IOException(e.toString());
                    }
                }
            }
        }

        // any new objects created?
        if (object != null)
        {
            // now descend to children nodes and create/ populate them.
            List children = node.elements();
            HashMap props = new HashMap();

            for (Iterator i = children.iterator();i.hasNext();)
            {
                Element child = (Element) i.next();
                Object value = configureObject( child, object );

                String key = toJavaNameConvention(child.getName(),false);
                if (props.containsKey(key))
                {
                    Object v = props.get(key);
                    if (v instanceof List)
                    {
                        ((List) v).add(value);
                    }
                    else
                    {
                        List l = new LinkedList();
                        l.add(v);
                        l.add(value);
                        props.put(key, l);
                    }
                }
                else
                {
                    props.put(key, value);
                }
            }

            // populate the current node's properties with children - create
            // array properties if needed.
            try
            {
                for(Iterator names = props.keySet().iterator(); names.hasNext();)
                {
                    String name = (String)names.next();
                    if(name != null)
                    {
                        Object value = props.get(name);
                        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(object, name);
                        if (descriptor == null)
                        {
                            if (failOnUnrecognizedProperties)
                                throw new IOException("No property setter for property "
                                    + name + " on object " + object.getClass());

                        }
                        else
                        {
                            Method m = descriptor.getWriteMethod();
                            if (m == null)
                            	throw new IOException("No setter method for property: " + name);

                            if (m.getParameterTypes().length == 1)
                            {
                                if (m.getParameterTypes()[0].isArray())
                                {
                                    Object [] arrayValue;
                                    if (value instanceof List)
                                    {
                                        arrayValue = new Object [ ((List) value).size() ];
                                        ((List) value).toArray( arrayValue );
                                    }
                                    else
                                    {
                                        arrayValue = new Object [] { value };
                                    }

                                    Object [] newArray = (Object [])
                                        java.lang.reflect.Array.newInstance(m.getParameterTypes()[0].getComponentType(),
                                            arrayValue.length);
                                    System.arraycopy(arrayValue, 0, newArray, 0, arrayValue.length);

                                    BeanUtils.setProperty(object, name, newArray);
                                }
                                else
                                {
                                    // simple type property setter
                                    BeanUtils.setProperty(object, name, value);
                                }
                            }
                            else
                                throw new IOException("Property setter method with no arguments?");
                        }
                    }
                }

            }
            catch (IllegalAccessException e)
            {
                throw new IOException(e.toString());
            }
            catch (InvocationTargetException e)
            {
                throw new IOException("Exception inside component initialization method: "
                    + com.dawidweiss.carrot.remote.controller.util.ExceptionHelper.getStackTrace(e.getTargetException()));
            }
            catch (NoSuchMethodException e)
            {
                throw new IOException(e.toString());
            }

            // configure the class/ interface if it implements PostConfigureCheck
            // interface
            if (object instanceof PostConfigureCheck)
            {
                String s = ((PostConfigureCheck) object).assertConfigured();
                if (s!=null)
                    throw new IOException(s);
            }

            if (node.attribute("id") != null)
            {
                String key = node.attributeValue("id");
                if (objectsRepository.containsKey(key))
                    throw new IOException("Node IDs must be unique.");

                objectsRepository.put(key, object);
            }

            return object;
        }
        else
        {
            // no new objects created - attribute node?
            // check if this element has any children - if yes, throw an exception -
            // unrecognized complex node. If no, check if its body can be treated
            // as property's value and populate parent with it.
            if (!node.elements().isEmpty())
                throw new IOException("Don't know how to instantiate node: "
                    + node.getName());
            if ("".equals( node.getText() ))
                throw new IOException("Node has no value (text).");

            return node.getText();
        }
    }


    private final static String toJavaNameConvention(String name, boolean upperFirst)
    {
        final int size = name.length();
        char[] chars   = name.toCharArray();
        int pos = 0;

        boolean nextUppercase = upperFirst;
        for (int i=0;i<size;i++)
        {
            if (chars[i]=='-')
            {
                nextUppercase = true;
                continue;
            }
            else
            {
                chars[pos] = nextUppercase ? Character.toUpperCase(chars[i])
                                           : chars[i];
                pos++;
            }

            nextUppercase = false;
        }
        return new String(chars,0,pos);
    }

    public boolean isFailOnUnrecognizedProperties()
    {
        return failOnUnrecognizedProperties;
    }

    public void setFailOnUnrecognizedProperties(boolean b)
    {
        failOnUnrecognizedProperties = b;
    }

}
