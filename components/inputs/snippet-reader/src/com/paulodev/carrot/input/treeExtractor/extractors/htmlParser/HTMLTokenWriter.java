

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


package com.paulodev.carrot.input.treeExtractor.extractors.htmlParser;


/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2002 Dawid Weiss, Institute of Computing Science, Poznan University of
 * Technology
 * </p>
 * 
 * <p>
 * Company: Institute of Computing Science, Poznan University of Technology
 * </p>
 */
public interface HTMLTokenWriter
{
    public void openingTag(StringBuffer sb);


    public void closingTag(StringBuffer sb);


    public void childrenTags(StringBuffer sb);
}
