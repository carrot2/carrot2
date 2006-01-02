
// Generated file. Do not edit by hand.

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * Service must be the root element of the component descriptor
 * file.
 * One service may incorporate several components.
 *             
 * 
 * @version $Revision$ $Date$
 */
public class Service implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _framework
     */
    private java.lang.String _framework = "Carrot2";

    /**
     * Field _componentDescriptorList
     */
    private java.util.Vector _componentDescriptorList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Service() {
        super();
        setFramework("Carrot2");
        _componentDescriptorList = new Vector();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Service()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addComponentDescriptor
     * 
     * @param vComponentDescriptor
     */
    public void addComponentDescriptor(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor vComponentDescriptor)
        throws java.lang.IndexOutOfBoundsException
    {
        _componentDescriptorList.addElement(vComponentDescriptor);
    } //-- void addComponentDescriptor(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) 

    /**
     * Method addComponentDescriptor
     * 
     * @param index
     * @param vComponentDescriptor
     */
    public void addComponentDescriptor(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor vComponentDescriptor)
        throws java.lang.IndexOutOfBoundsException
    {
        _componentDescriptorList.insertElementAt(vComponentDescriptor, index);
    } //-- void addComponentDescriptor(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) 

    /**
     * Method enumerateComponentDescriptor
     */
    public java.util.Enumeration enumerateComponentDescriptor()
    {
        return _componentDescriptorList.elements();
    } //-- java.util.Enumeration enumerateComponentDescriptor() 

    /**
     * Method getComponentDescriptor
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor getComponentDescriptor(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _componentDescriptorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) _componentDescriptorList.elementAt(index);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor getComponentDescriptor(int) 

    /**
     * Method getComponentDescriptor
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor[] getComponentDescriptor()
    {
        int size = _componentDescriptorList.size();
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor[] mArray = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) _componentDescriptorList.elementAt(index);
        }
        return mArray;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor[] getComponentDescriptor() 

    /**
     * Method getComponentDescriptorCount
     */
    public int getComponentDescriptorCount()
    {
        return _componentDescriptorList.size();
    } //-- int getComponentDescriptorCount() 

    /**
     * Method getFrameworkReturns the value of field 'framework'.
     * 
     * @return the value of field 'framework'.
     */
    public java.lang.String getFramework()
    {
        return this._framework;
    } //-- java.lang.String getFramework() 

    /**
     * Method isValid
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeAllComponentDescriptor
     */
    public void removeAllComponentDescriptor()
    {
        _componentDescriptorList.removeAllElements();
    } //-- void removeAllComponentDescriptor() 

    /**
     * Method removeComponentDescriptor
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor removeComponentDescriptor(int index)
    {
        java.lang.Object obj = _componentDescriptorList.elementAt(index);
        _componentDescriptorList.removeElementAt(index);
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) obj;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor removeComponentDescriptor(int) 

    /**
     * Method setComponentDescriptor
     * 
     * @param index
     * @param vComponentDescriptor
     */
    public void setComponentDescriptor(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor vComponentDescriptor)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _componentDescriptorList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _componentDescriptorList.setElementAt(vComponentDescriptor, index);
    } //-- void setComponentDescriptor(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) 

    /**
     * Method setComponentDescriptor
     * 
     * @param componentDescriptorArray
     */
    public void setComponentDescriptor(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor[] componentDescriptorArray)
    {
        //-- copy array
        _componentDescriptorList.removeAllElements();
        for (int i = 0; i < componentDescriptorArray.length; i++) {
            _componentDescriptorList.addElement(componentDescriptorArray[i]);
        }
    } //-- void setComponentDescriptor(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) 

    /**
     * Method setFrameworkSets the value of field 'framework'.
     * 
     * @param framework the value of field 'framework'.
     */
    public void setFramework(java.lang.String framework)
    {
        this._framework = framework;
    } //-- void setFramework(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Service unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Service) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Service.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Service unmarshal(java.io.Reader) 

    /**
     * Method validate
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
