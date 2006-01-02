
// Generated file. Do not edit by hand.

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * This element defines a process of query evaluation for Carrot2
 * controller component.
 * Process may be either a specification of processing chain
 * between components,
 * or a fully-fledged scripted program.
 *             
 * 
 * @version $Revision$ $Date$
 */
public class ProcessDescriptor implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _id
     */
    private java.lang.String _id;

    /**
     * Field _description
     */
    private java.lang.String _description;

    /**
     * Field _hidden
     */
    private boolean _hidden;

    /**
     * keeps track of state for field: _hidden
     */
    private boolean _has_hidden;

    /**
     * Field _processingScript
     */
    private com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript _processingScript;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProcessDescriptor() {
        super();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteHidden
     */
    public void deleteHidden()
    {
        this._has_hidden= false;
    } //-- void deleteHidden() 

    /**
     * Method getDescriptionReturns the value of field
     * 'description'.
     * 
     * @return the value of field 'description'.
     */
    public java.lang.String getDescription()
    {
        return this._description;
    } //-- java.lang.String getDescription() 

    /**
     * Method getHiddenReturns the value of field 'hidden'.
     * 
     * @return the value of field 'hidden'.
     */
    public boolean getHidden()
    {
        return this._hidden;
    } //-- boolean getHidden() 

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
     * Method getProcessingScriptReturns the value of field
     * 'processingScript'.
     * 
     * @return the value of field 'processingScript'.
     */
    public com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript getProcessingScript()
    {
        return this._processingScript;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript getProcessingScript() 

    /**
     * Method hasHidden
     */
    public boolean hasHidden()
    {
        return this._has_hidden;
    } //-- boolean hasHidden() 

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
     * Method setDescriptionSets the value of field 'description'.
     * 
     * @param description the value of field 'description'.
     */
    public void setDescription(java.lang.String description)
    {
        this._description = description;
    } //-- void setDescription(java.lang.String) 

    /**
     * Method setHiddenSets the value of field 'hidden'.
     * 
     * @param hidden the value of field 'hidden'.
     */
    public void setHidden(boolean hidden)
    {
        this._hidden = hidden;
        this._has_hidden = true;
    } //-- void setHidden(boolean) 

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
     * Method setProcessingScriptSets the value of field
     * 'processingScript'.
     * 
     * @param processingScript the value of field 'processingScript'
     */
    public void setProcessingScript(com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript processingScript)
    {
        this._processingScript = processingScript;
    } //-- void setProcessingScript(com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessDescriptor unmarshal(java.io.Reader) 

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
