/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.normalizer;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Brings the case of all tokens in all input tokenized documents's titles and
 * snippets to one common form. This process can be thought of as 'stemming for
 * case'.
 * 
 * All input tokens must be subclasses of
 * {@link com.dawidweiss.carrot.util.tokenizer.parser.StringTypedToken}
 * interface. The input documents will get <b>modified </b>--their tokens will
 * get overwritten with case-normalized versions. Token types will be preserved.
 * No support is provided for the full text of documents. This class is <b>not
 * </b> thread-safe.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface CaseNormalizer
{
    /**
     * Clears this instance so that it can be reused with another set of
     * documents.
     */
    public void clear();

    /**
     * Adds a document to the normalization engine.
     * 
     * @throws IllegalStateException when an attempt is made to add documents
     *             after the {@link #getNormalizedDocuments()}has been called.
     * @param document
     */
    public void addDocument(TokenizedDocument document);

    /**
     * Returns a List of case normalized documents. After a successful call to
     * this method, no documents can be added until this case normalizer is
     * cleared using the {@link #clear()}method. Note: it is in this method
     * that document's tokenks get modified.
     * 
     * @return a List of case normalized documents
     */
    public List getNormalizedDocuments();
}