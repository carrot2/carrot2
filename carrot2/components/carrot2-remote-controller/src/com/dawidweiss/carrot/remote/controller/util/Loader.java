package com.dawidweiss.carrot.remote.controller.util;



import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.beanutils.*;


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
        // first prototype we use JDOM. Ideally a SAX-based solution
        // would be needed here.

        try
        {
            org.jdom.Element root =
                new org.jdom.input.SAXBuilder(validation)
                    .build(XMLStream).getRootElement();

            return configureObject( root, null );
        }
        catch (org.jdom.JDOMException e)
        {
            throw new IOException("Cannot load components: " + e.toString());
        }
    }

    private final Object configureObject( org.jdom.Element node, Object parent )
        throws IOException
    {
        String nodeName = node.getName();
        Object mapping;
        Object object = null;

        // check if the node has 'refid' attribute. if yes, try the repository first.
        if (node.getAttribute("refid")!=null)
        {
            if (!node.getChildren().isEmpty())
                throw new IOException("Referenced node " + nodeName
                    + " cannot be redefined or contain new attributes.");

            String refid = node.getAttributeValue("refid");
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
                if (node.getAttribute("class")==null)
                    throw new IOException("Missing class attribute in mapping for interface "
                        + imapping.getInterfaceName());

                String interfaceInstanceClassName =
                    node.getAttribute("class").getValue().trim();
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
            List children = node.getChildren();
            HashMap props = new HashMap();

            for (Iterator i = children.iterator();i.hasNext();)
            {
                org.jdom.Element child = (org.jdom.Element) i.next();
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

            if (node.getAttribute("id") != null)
            {
                String key = node.getAttributeValue("id");
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
            if (!node.getChildren().isEmpty())
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
