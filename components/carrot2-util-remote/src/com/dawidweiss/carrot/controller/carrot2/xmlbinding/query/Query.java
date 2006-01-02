
// Generated file. Do not edit by hand.

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.query;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class Query.
 * 
 * @version $Revision$ $Date$
 */
public class Query extends QueryType 
implements java.io.Serializable
{


      //----------------/
     //- Constructors -/
    //----------------/

    public Query() {
        super();
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Method unmarshal
     * 
     * @param reader
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query) Unmarshaller.unmarshal(com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query.class, reader);
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.Query unmarshal(java.io.Reader) 

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
