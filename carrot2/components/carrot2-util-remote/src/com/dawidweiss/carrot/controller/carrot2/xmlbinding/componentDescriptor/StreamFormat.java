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

/**
 * 
 * Data stream format declaration consists of a MIME type and
 * optional restrictions
 * about when this data format is accepted or produced by the
 * component.
 * Restrictions may for example indicate that the data format is
 * only accepted when
 * certain POST header parameter is set to a predefined constant
 * value.
 *             
 * 
 * @version $Revision$ $Date$
 */
public abstract class StreamFormat implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _mime
     */
    private java.lang.String _mime;

    /**
     * Field _restrictionsList
     */
    private java.util.Vector _restrictionsList;


      //----------------/
     //- Constructors -/
    //----------------/

    public StreamFormat() {
        super();
        _restrictionsList = new Vector();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.StreamFormat()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addRestrictions
     * 
     * @param vRestrictions
     */
    public void addRestrictions(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions vRestrictions)
        throws java.lang.IndexOutOfBoundsException
    {
        _restrictionsList.addElement(vRestrictions);
    } //-- void addRestrictions(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) 

    /**
     * Method addRestrictions
     * 
     * @param index
     * @param vRestrictions
     */
    public void addRestrictions(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions vRestrictions)
        throws java.lang.IndexOutOfBoundsException
    {
        _restrictionsList.insertElementAt(vRestrictions, index);
    } //-- void addRestrictions(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) 

    /**
     * Method enumerateRestrictions
     */
    public java.util.Enumeration enumerateRestrictions()
    {
        return _restrictionsList.elements();
    } //-- java.util.Enumeration enumerateRestrictions() 

    /**
     * Method getMimeReturns the value of field 'mime'.
     * 
     * @return the value of field 'mime'.
     */
    public java.lang.String getMime()
    {
        return this._mime;
    } //-- java.lang.String getMime() 

    /**
     * Method getRestrictions
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions getRestrictions(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _restrictionsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) _restrictionsList.elementAt(index);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions getRestrictions(int) 

    /**
     * Method getRestrictions
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions[] getRestrictions()
    {
        int size = _restrictionsList.size();
        com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions[] mArray = new com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) _restrictionsList.elementAt(index);
        }
        return mArray;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions[] getRestrictions() 

    /**
     * Method getRestrictionsCount
     */
    public int getRestrictionsCount()
    {
        return _restrictionsList.size();
    } //-- int getRestrictionsCount() 

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
     * Method removeAllRestrictions
     */
    public void removeAllRestrictions()
    {
        _restrictionsList.removeAllElements();
    } //-- void removeAllRestrictions() 

    /**
     * Method removeRestrictions
     * 
     * @param index
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions removeRestrictions(int index)
    {
        java.lang.Object obj = _restrictionsList.elementAt(index);
        _restrictionsList.removeElementAt(index);
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) obj;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions removeRestrictions(int) 

    /**
     * Method setMimeSets the value of field 'mime'.
     * 
     * @param mime the value of field 'mime'.
     */
    public void setMime(java.lang.String mime)
    {
        this._mime = mime;
    } //-- void setMime(java.lang.String) 

    /**
     * Method setRestrictions
     * 
     * @param index
     * @param vRestrictions
     */
    public void setRestrictions(int index, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions vRestrictions)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _restrictionsList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _restrictionsList.setElementAt(vRestrictions, index);
    } //-- void setRestrictions(int, com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) 

    /**
     * Method setRestrictions
     * 
     * @param restrictionsArray
     */
    public void setRestrictions(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions[] restrictionsArray)
    {
        //-- copy array
        _restrictionsList.removeAllElements();
        for (int i = 0; i < restrictionsArray.length; i++) {
            _restrictionsList.addElement(restrictionsArray[i]);
        }
    } //-- void setRestrictions(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.Restrictions) 

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
