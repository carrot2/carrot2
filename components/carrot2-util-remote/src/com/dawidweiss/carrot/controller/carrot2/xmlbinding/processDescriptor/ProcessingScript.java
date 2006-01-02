
// Generated file. Do not edit by hand.

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * 
 * A scripted process contains a program to be evaluated for each
 * user query. The
 * program can be written in any of BSF-aware scripting dialects
 * (such as Beanshell
 * or Javascript). There exists a bunch of globally accessible
 * variables (such as query
 * and components), which the script may utilize to produce the
 * final output. 
 *             
 * 
 * @version $Revision$ $Date$
 */
public class ProcessingScript implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
    private java.lang.String _content = "";

    /**
     * Field _language
     */
    private java.lang.String _language;


      //----------------/
     //- Constructors -/
    //----------------/

    public ProcessingScript() {
        super();
        setContent("");
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getContentReturns the value of field 'content'. The
     * field 'content' has the following description: internal
     * content storage
     * 
     * @return the value of field 'content'.
     */
    public java.lang.String getContent()
    {
        return this._content;
    } //-- java.lang.String getContent() 

    /**
     * Method getLanguageReturns the value of field 'language'.
     * 
     * @return the value of field 'language'.
     */
    public java.lang.String getLanguage()
    {
        return this._language;
    } //-- java.lang.String getLanguage() 

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
     * Method setContentSets the value of field 'content'. The
     * field 'content' has the following description: internal
     * content storage
     * 
     * @param content the value of field 'content'.
     */
    public void setContent(java.lang.String content)
    {
        this._content = content;
    } //-- void setContent(java.lang.String) 

    /**
     * Method setLanguageSets the value of field 'language'.
     * 
     * @param language the value of field 'language'.
     */
    public void setLanguage(java.lang.String language)
    {
        this._language = language;
    } //-- void setLanguage(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.processDescriptor.ProcessingScript unmarshal(java.io.Reader) 

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
