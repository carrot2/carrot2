/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.2</a>, using an XML
 * Schema.
 * $Id$
 */

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Vector;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Restrictions.
 * 
 * @version $Revision$ $Date$
 */
public class Restrictions implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _postParamList
     */
    private java.util.Vector _postParamList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Restrictions() {
        super();
        _postParamList = new Vector();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPostParam
     * 
     * @param vPostParam
     */
    public void addPostParam(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam vPostParam)
        throws java.lang.IndexOutOfBoundsException
    {
        _postParamList.addElement(vPostParam);
    } //-- void addPostParam(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) 

    /**
     * Method addPostParam
     * 
     * @param index
     * @param vPostParam
     */
    public void addPostParam(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam vPostParam)
        throws java.lang.IndexOutOfBoundsException
    {
        _postParamList.insertElementAt(vPostParam, index);
    } //-- void addPostParam(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) 

    /**
     * Method enumeratePostParam
     */
    public java.util.Enumeration enumeratePostParam()
    {
        return _postParamList.elements();
    } //-- java.util.Enumeration enumeratePostParam() 

    /**
     * Method getPostParam
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam getPostParam(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _postParamList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) _postParamList.elementAt(index);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam getPostParam(int) 

    /**
     * Method getPostParam
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam[] getPostParam()
    {
        int size = _postParamList.size();
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam[] mArray = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) _postParamList.elementAt(index);
        }
        return mArray;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam[] getPostParam() 

    /**
     * Method getPostParamCount
     */
    public int getPostParamCount()
    {
        return _postParamList.size();
    } //-- int getPostParamCount() 

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
     * Method removeAllPostParam
     */
    public void removeAllPostParam()
    {
        _postParamList.removeAllElements();
    } //-- void removeAllPostParam() 

    /**
     * Method removePostParam
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam removePostParam(int index)
    {
        java.lang.Object obj = _postParamList.elementAt(index);
        _postParamList.removeElementAt(index);
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) obj;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam removePostParam(int) 

    /**
     * Method setPostParam
     * 
     * @param index
     * @param vPostParam
     */
    public void setPostParam(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam vPostParam)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _postParamList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _postParamList.setElementAt(vPostParam, index);
    } //-- void setPostParam(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) 

    /**
     * Method setPostParam
     * 
     * @param postParamArray
     */
    public void setPostParam(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam[] postParamArray)
    {
        //-- copy array
        _postParamList.removeAllElements();
        for (int i = 0; i < postParamArray.length; i++) {
            _postParamList.addElement(postParamArray[i]);
        }
    } //-- void setPostParam(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.PostParam) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions unmarshal(java.io.Reader) 

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
