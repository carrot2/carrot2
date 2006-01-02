
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.tokenizer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TokenMgrError extends Error {
    /*
     * Ordinals for various reasons why an Error of this type can be thrown.
     */

    /**
     * Lexical error occured.
     */
    static final int LEXICAL_ERROR = 0;

    /**
     * An attempt wass made to create a second instance of a static token
     * manager.
     */
    static final int STATIC_LEXER_ERROR = 1;

    /**
     * Tried to change to an invalid lexical state.
     */
    static final int INVALID_LEXICAL_STATE = 2;

    /**
     * Detected (and bailed out of) an infinite loop in the token manager.
     */
    static final int LOOP_DETECTED = 3;

    /**
     * Indicates the reason why the exception is thrown. It will have one of
     * the above 4 values.
     */
    int errorCode;

    /**
     * Replaces unprintable characters by their espaced (or unicode escaped)
     * equivalents in the given string
     */
    protected static final String addEscapes(String str) {
        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case 0:

                continue;

            case '\b':
                retval.append("\\b");

                continue;

            case '\t':
                retval.append("\\t");

                continue;

            case '\n':
                retval.append("\\n");

                continue;

            case '\f':
                retval.append("\\f");

                continue;

            case '\r':
                retval.append("\\r");

                continue;

            case '\"':
                retval.append("\\\"");

                continue;

            case '\'':
                retval.append("\\\'");

                continue;

            case '\\':
                retval.append("\\\\");

                continue;

            default:

                if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" +
                        s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }

                continue;
            }
        }

        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the token
     * manager to indicate a lexical error. Parameters :  EOFSeen     :
     * indicates if EOF caused the lexicl error curLexState : lexical state in
     * which this error occured errorLine   : line number when the error
     * occured errorColumn : column number when the error occured errorAfter
     * : prefix that was seen before this error occured curchar     : the
     * offending character Note: You can customize the lexical error message
     * by modifying this method.
     */
    private static final String LexicalError(boolean EOFSeen, int lexState,
        int errorLine, int errorColumn, String errorAfter, char curChar) {
        return ("Lexical error at line " + errorLine + ", column " +
        errorColumn + ".  Encountered: " +
        (EOFSeen ? "<EOF> "
                 : (("\"" + addEscapes(String.valueOf(curChar)) + "\"") + " (" +
        (int) curChar + "), ")) + "after : \"" + addEscapes(errorAfter) + "\"");
    }

    /**
     * You can also modify the body of this method to customize your error
     * messages. For example, cases like LOOP_DETECTED and
     * INVALID_LEXICAL_STATE are not of end-users concern, so you can return
     * something like :  "Internal Error : Please file a bug report .... "
     * from this method for such cases in the release version of your parser.
     */
    public String getMessage() {
        return super.getMessage();
    }

    /*
     * Constructors of various flavors follow.
     */
    public TokenMgrError() {
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param message DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(String message, int reason) {
        super(message);
        errorCode = reason;
    }

    /**
     * Creates a new TokenMgrError object.
     *
     * @param EOFSeen DOCUMENT ME!
     * @param lexState DOCUMENT ME!
     * @param errorLine DOCUMENT ME!
     * @param errorColumn DOCUMENT ME!
     * @param errorAfter DOCUMENT ME!
     * @param curChar DOCUMENT ME!
     * @param reason DOCUMENT ME!
     */
    public TokenMgrError(boolean EOFSeen, int lexState, int errorLine,
        int errorColumn, String errorAfter, char curChar, int reason) {
        this(LexicalError(EOFSeen, lexState, errorLine, errorColumn,
                errorAfter, curChar), reason);
    }
}
