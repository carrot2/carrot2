package com.dawidweiss.carrot.util.jdom;

import java.io.*;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;


/**
 * Static methods helping in traversing JDOM's Elements structure.
 *
 * @author Dawid Weiss
 */
public class JDOMHelper
{

    /** No instantiation needed - all methods are static anyway, but
     *  if somebody insists (for the sake of shorter names or performance
     *  issues - dynamic calls are faster than static invocation) */
    public JDOMHelper()
    {
    }


    /**
     * Retrieves a JDOM's Element object from a specified 'path'. Starting from root,
     * children must be delimited by '/' characters, for instance: "general/log4j/layout".
     *
     * The first path's element must be named the same as the root argument passed to
     * this function.
     *
     * Returns null if no element has been found. No starting '/' is needed.
     */
    public static Element getElement(String path, Element current) {
        // root node?
        path = path.trim();

        if (path.charAt(0) == '/')
            path = path.substring(1);

        if (current==null || "".equals(path))
            return current;

        // descend
        StringTokenizer st = new StringTokenizer( path, "/");
        String expected    = st.nextToken();

        if (current.getName().equals( expected ) == false) {
            // root node doesn't match.
            return null;
        }

        while (st.hasMoreTokens()) {
            expected = st.nextToken();
            current = current.getChild(expected);
            if (current == null)
                return null;
        }

        return current;
    }


    /**
     * Retrieves an array of elements of a given name at the end of the path.
     */
    public static List getElements( String path, Element current ) {
        Element someChild = getElement( path, current );
        if (someChild != null)
        {
            // this is tricky, but will work for all nodes if they have a
            // parent (are not the root).
            if (someChild.getParent() != null && someChild.getParent() instanceof Element)
                return ((Element) someChild.getParent())
                                    .getChildren( someChild.getName() );
            else
            {
                LinkedList oneElement = new LinkedList();
                oneElement.add( someChild );
                return oneElement;
            }
        }
        else
        return null;
    }


    /**
     * Extracts the string of an element, or an attribute from JDOM's XML tree.
     * The use of this class is file-system -- like. Paths are delimited with '/'
     * characters, attributes separated from paths using hash '#' characters.
     * For instance, if passed the root of JDOM's tree: "start/beginwith#attribute"
     * will descend into start element, look for beginwith element and
     * search for its attribute 'attribute'.
     *
     * @return a string or null if not succeeded and exceptionIfNotPresent not set.
     * @throws IllegalArgumentException if exceptionIfNotPresent argument is set to
     *                         true and the path could not be found.
     */
    public static String getStringFromJDOM(String path, Element root, boolean exceptionIfNotPresent) {
        int attributeSelector = path.lastIndexOf('#');
        String element;

        if (attributeSelector == -1)
        {
            element = path;
        }
        else
        {
            element = path.substring( 0, attributeSelector );
        }

        Element e = getElement( element, root );

        if (e==null) {
            if (exceptionIfNotPresent) {
                throw new IllegalArgumentException("Path element: " + element + " not present in: " + root);
            }
            return null;
        }

        if (attributeSelector != -1) {
            try {
                String attrName = path.substring( attributeSelector+1 );
                Attribute attr = e.getAttribute(attrName);

                if (attr == null) {
                    if (exceptionIfNotPresent) {
                        throw new IllegalArgumentException("Attribute " + attrName + " for element: " + element + " not present in: " + root);
                    }
                    return null;
                }
                return attr.getValue();
            } catch (ArrayIndexOutOfBoundsException l) {
                throw new IllegalArgumentException("Empty attribute name is illegal.");
            }
        } else {
            return e.getText();
        }
    }


    /**
     * Extracts the string of an element, or an attribute from JDOM's XML tree.
     * Same as getStringFromJDOM(String,Element,boolean), but instead of returning
     * null for non-existing paths/ values, it returns the default value.
     *
     * @return A string or default value of the element/attribute.
     */
    public static String getStringFromJDOM(String path, Element root, String defaultValue)
    {
        String val = getStringFromJDOM( path, root, false );
        return (val!=null ? val : defaultValue);
    }


    /**
     * Builds a Document from an xml in a string.
     */
    public static Document buildDocumentFromString( String xml, boolean validation )
        throws JDOMException, IOException
    {
        SAXBuilder builder = new org.jdom.input.SAXBuilder( validation );
        return builder.build( new StringReader( xml ) );
    }


    /**
     * Builds a Document from a stream.
     */
    public static Document buildDocumentFromStream( InputStream xmlStream, boolean validation )
        throws JDOMException, IOException
    {
        SAXBuilder builder = new org.jdom.input.SAXBuilder( validation );
        return builder.build( xmlStream );
    }


}