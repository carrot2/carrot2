/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */

package com.dawidweiss.carrot.input.nutch;

final class XMLSerializerHelper {

    /**
     * Can instantiate for faster local access.
     */
    /*package*/ XMLSerializerHelper() {
    }

    /**
     * Escapes a string so that it can be safely put into an XML
     * stream as an attribute.
     *
     * This implementation copied from JDOM project code:
     */
    public String escapeAttributeEntities(String str) {
        StringBuffer buffer = null;

        final int length = str.length();
        String entity;
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);

            switch (ch) {
            case 60: // '<'
                entity = "&lt;";
                break;
            case 62: // '>'
                entity = "&gt;";
                break;
            case 34: // '"'
                entity = "&quot;";
                break;
            case 38: // '&'
                entity = "&amp;";
                break;
            default:
                if (buffer != null)
                    buffer.append(ch);
                continue;
            }

            if (buffer == null) {
                buffer = new StringBuffer(str.length() + 20);
                buffer.append(str.substring(0, i));
                buffer.append(entity);
            } else {
                buffer.append(entity);
            }
        }

        return (buffer != null) ? buffer.toString() : str;
    }

    /**
     * Escapes a string so that it can be safely put into an XML Element as an attribute.
     *
     * This implementation copied from JDOM project code.
     */
    public String escapeElementEntities(String str) {
        StringBuffer buffer = null;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            String entity;

            switch (ch) {
            case 60: // '<'
                entity = "&lt;";

                break;

            case 62: // '>'
                entity = "&gt;";

                break;

            case 38: // '&'
                entity = "&amp;";

                break;

            default:
                entity = null;

                break;
            }

            if (buffer == null) {
                if (entity != null) {
                    buffer = new StringBuffer(str.length() + 20);
                    buffer.append(str.substring(0, i));
                    buffer.append(entity);
                }
            } else if (entity == null) {
                buffer.append(ch);
            } else {
                buffer.append(entity);
            }
        }

        return (buffer != null) ? buffer.toString() : str;
    }
}
