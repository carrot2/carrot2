/**
 * 
 * @author chilang
 * Created 2003-07-28, 16:22:19.
 */
package com.chilang.carrot.filter.cluster.rough.data;

import java.util.StringTokenizer;

public class TokenizerFactory {

    public static final String DELIMITERS = " \t\n\r\f-+=.,:;!?&#()[]/\\";

    public static StringTokenizer getStringTokenizer(String text) {
        return new StringTokenizer(text, DELIMITERS);
    }
}
