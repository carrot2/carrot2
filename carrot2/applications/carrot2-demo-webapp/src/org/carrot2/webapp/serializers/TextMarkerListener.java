
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

package org.carrot2.webapp.serializers;


/**
 * @author stachoo
 */
public interface TextMarkerListener
{
    /**
     * Triggered for fragments of text that have matched some tokenization rule.
     * The <code>id</code> can still be null (e.g. for stop words).
     */
    public void markedTextIdentified(char[] text, int startPosition,
            int length, String id, boolean newId); 


    /**
     * Triggered for fragments of text that have not matched any tokenization
     * rules (e.g. white space).
     */
    public void unmarkedTextIdentified(char[] text, int startPosition,
            int length);
}
