/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.4.2</a>, using an XML
 * Schema.
 * $Id$
 */

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.query;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/


/**
 * The body of user query
 *             (unrestricted text).
 * 
 * @version $Revision$ $Date$
 */
public abstract class QueryType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * internal content storage
     */
    private java.lang.String _content = "";

    /**
     * The number of requested results for the query. The default
     * value of this element
     * may be different than the one present in this schema and
     * depends on the settings
     * of the controller component.
     *  
     */
    private int _requestedResults = 100;

    /**
     * keeps track of state for field: _requestedResults
     */
    private boolean _has_requestedResults;


      //----------------/
     //- Constructors -/
    //----------------/

    public QueryType() {
        super();
        setContent("");
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.query.QueryType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteRequestedResults
     */
    public void deleteRequestedResults()
    {
        this._has_requestedResults= false;
    } //-- void deleteRequestedResults() 

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
     * Method getRequestedResultsReturns the value of field
     * 'requestedResults'. The field 'requestedResults' has the
     * following description: The number of requested results for
     * the query. The default value of this element
     * may be different than the one present in this schema and
     * depends on the settings
     * of the controller component.
     *  
     * 
     * @return the value of field 'requestedResults'.
     */
    public int getRequestedResults()
    {
        return this._requestedResults;
    } //-- int getRequestedResults() 

    /**
     * Method hasRequestedResults
     */
    public boolean hasRequestedResults()
    {
        return this._has_requestedResults;
    } //-- boolean hasRequestedResults() 

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
     * Method setRequestedResultsSets the value of field
     * 'requestedResults'. The field 'requestedResults' has the
     * following description: The number of requested results for
     * the query. The default value of this element
     * may be different than the one present in this schema and
     * depends on the settings
     * of the controller component.
     *  
     * 
     * @param requestedResults the value of field 'requestedResults'
     */
    public void setRequestedResults(int requestedResults)
    {
        this._requestedResults = requestedResults;
        this._has_requestedResults = true;
    } //-- void setRequestedResults(int) 

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
