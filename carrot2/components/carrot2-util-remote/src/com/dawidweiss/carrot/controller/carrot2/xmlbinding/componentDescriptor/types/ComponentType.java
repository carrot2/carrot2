
// Generated file. Do not edit by hand.

package com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Class ComponentType.
 * 
 * @version $Revision$ $Date$
 */
public class ComponentType implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The input type
     */
    public static final int INPUT_TYPE = 0;

    /**
     * The instance of the input type
     */
    public static final ComponentType INPUT = new ComponentType(INPUT_TYPE, "input");

    /**
     * The output type
     */
    public static final int OUTPUT_TYPE = 1;

    /**
     * The instance of the output type
     */
    public static final ComponentType OUTPUT = new ComponentType(OUTPUT_TYPE, "output");

    /**
     * The filter type
     */
    public static final int FILTER_TYPE = 2;

    /**
     * The instance of the filter type
     */
    public static final ComponentType FILTER = new ComponentType(FILTER_TYPE, "filter");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private ComponentType(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerateReturns an enumeration of all possible
     * instances of ComponentType
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getTypeReturns the type of this ComponentType
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("input", INPUT);
        members.put("output", OUTPUT);
        members.put("filter", FILTER);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method toStringReturns the String representation of this
     * ComponentType
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOfReturns a new ComponentType based on the given
     * String value.
     * 
     * @param string
     */
    public static com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid ComponentType";
            throw new IllegalArgumentException(err);
        }
        return (ComponentType) obj;
    } //-- com.dawidweiss.carrot.controller.carrot2.xmlbinding.componentDescriptor.types.ComponentType valueOf(java.lang.String) 

}
