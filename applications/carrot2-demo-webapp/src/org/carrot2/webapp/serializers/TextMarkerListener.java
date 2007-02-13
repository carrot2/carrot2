/*
 * Copyright (c) 2004 Poznan Supercomputing and Networking Center
 * 10 Noskowskiego Street, Poznan, Wielkopolska 61-704, Poland
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Poznan Supercomputing and Networking Center ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into
 * with PSNC.
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
