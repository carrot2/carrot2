
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util;


/**
 * Escapes a string so that is is ready to be "pasted" into
 * Javascript code (as the content of a quoted string).
 * 
 * @author Dawid Weiss
 */
public final class EscapeString {

    private EscapeString() {
    }

    public static String escapeString(String s) {
        int i = 0;
        int j = 0;
        int max = s.length();
        char chars[] = new char[max * 2];
        while(i < max) {
            char x = s.charAt(i++);
            switch(x) {
                case 9: // '\t'
                    x = 't';
                    break;
                case 10: // '\n'
                    x = 'n';
                    break;
                case 13: // '\r'
                    x = 'r';
                    break;
                case '\\': // '\\'
                    x = '\\';
                    break;
                case 34: // '"'
                    break;
                default:
                    chars[j++] = x;
                    continue;
            }
            chars[j++] = '\\';
            chars[j++] = x;
        }
        return new String(chars, 0, j);
    }
}
