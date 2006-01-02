
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
 * Components define a single data processing facility in Carrot2
 * architecture. Components consume input stream data and produce
 * some output data stream. Both input and output formats are
 * declared
 * as MIME types.
 *             
 * 
 * @version $Revision$ $Date$
 */
public class ComponentDescriptor implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _type
     */
    private com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType _type;

    /**
     * Field _serviceURL
     */
    private java.lang.String _serviceURL;

    /**
     * Field _configurationURL
     */
    private java.lang.String _configurationURL;

    /**
     * Field _infoURL
     */
    private java.lang.String _infoURL;

    /**
     * Field propertyChangeListeners
     */
    private java.util.Vector propertyChangeListeners;


      //----------------/
     //- Constructors -/
    //----------------/

    public ComponentDescriptor() {
        super();
        propertyChangeListeners = new Vector();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addPropertyChangeListenerRegisters a
     * PropertyChangeListener with this class.
     * 
     * @param pcl The PropertyChangeListener to register.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener pcl)
    {
        propertyChangeListeners.addElement(pcl);
    } //-- void addPropertyChangeListener(java.beans.PropertyChangeListener) 

    /**
     * Method getConfigurationURLReturns the value of field
     * 'configurationURL'.
     * 
     * @return the value of field 'configurationURL'.
     */
    public java.lang.String getConfigurationURL()
    {
        return this._configurationURL;
    } //-- java.lang.String getConfigurationURL() 

    /**
     * Method getIdReturns the value of field 'id'.
     * 
     * @return the value of field 'id'.
     */
    public java.lang.String getId()
    {
        return this._id;
    } //-- java.lang.String getId() 

    /**
     * Method getInfoURLReturns the value of field 'infoURL'.
     * 
     * @return the value of field 'infoURL'.
     */
    public java.lang.String getInfoURL()
    {
        return this._infoURL;
    } //-- java.lang.String getInfoURL() 

    /**
     * Method getServiceURLReturns the value of field 'serviceURL'.
     * 
     * @return the value of field 'serviceURL'.
     */
    public java.lang.String getServiceURL()
    {
        return this._serviceURL;
    } //-- java.lang.String getServiceURL() 

    /**
     * Method getTypeReturns the value of field 'type'.
     * 
     * @return the value of field 'type'.
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType getType()
    {
        return this._type;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType getType() 

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
     * Method notifyPropertyChangeListenersNotifies all registered
     * PropertyChangeListeners when a bound property's value
     * changes.
     * 
     * @param fieldName the name of the property that has changed.
     * @param newValue the new value of the property.
     * @param oldValue the old value of the property.
     */
    protected void notifyPropertyChangeListeners(java.lang.String fieldName, java.lang.Object oldValue, java.lang.Object newValue)
    {
        if (propertyChangeListeners == null) return;
        java.beans.PropertyChangeEvent event = new java.beans.PropertyChangeEvent(this, fieldName, oldValue, newValue);
        
        for (int i = 0; i < propertyChangeListeners.size(); i++) {
            ((java.beans.PropertyChangeListener) propertyChangeListeners.elementAt(i)).propertyChange(event);
        }
    } //-- void notifyPropertyChangeListeners(java.lang.String, java.lang.Object, java.lang.Object) 

    /**
     * Method removePropertyChangeListenerRemoves the given
     * PropertyChangeListener from this classes list of
     * ProperyChangeListeners.
     * 
     * @param pcl The PropertyChangeListener to remove.
     * @return true if the given PropertyChangeListener was removed.
     */
    public boolean removePropertyChangeListener(java.beans.PropertyChangeListener pcl)
    {
        return propertyChangeListeners.removeElement(pcl);
    } //-- boolean removePropertyChangeListener(java.beans.PropertyChangeListener) 

    /**
     * Method setConfigurationURLSets the value of field
     * 'configurationURL'.
     * 
     * @param configurationURL the value of field 'configurationURL'
     */
    public void setConfigurationURL(java.lang.String configurationURL)
    {
        this._configurationURL = configurationURL;
    } //-- void setConfigurationURL(java.lang.String) 

    /**
     * Method setIdSets the value of field 'id'.
     * 
     * @param id the value of field 'id'.
     */
    public void setId(java.lang.String id)
    {
        this._id = id;
    } //-- void setId(java.lang.String) 

    /**
     * Method setInfoURLSets the value of field 'infoURL'.
     * 
     * @param infoURL the value of field 'infoURL'.
     */
    public void setInfoURL(java.lang.String infoURL)
    {
        this._infoURL = infoURL;
    } //-- void setInfoURL(java.lang.String) 

    /**
     * Method setServiceURLSets the value of field 'serviceURL'.
     * 
     * @param serviceURL the value of field 'serviceURL'.
     */
    public void setServiceURL(java.lang.String serviceURL)
    {
        this._serviceURL = serviceURL;
    } //-- void setServiceURL(java.lang.String) 

    /**
     * Method setTypeSets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType type)
    {
        this._type = type;
    } //-- void setType(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.ComponentDescriptor unmarshal(java.io.Reader) 

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
