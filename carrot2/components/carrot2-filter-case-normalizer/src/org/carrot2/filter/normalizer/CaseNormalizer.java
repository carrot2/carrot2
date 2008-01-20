
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

package org.carrot2.filter.normalizer;

import java.util.*;

import org.carrot2.core.clustering.*;

/**
 * Brings the case of all tokens in all input tokenized documents's titles and
 * snippets to one common form. This process can be thought of as 'stemming for
 * case'.
 * 
 * All input tokens must be subclasses of
 * {@link org.carrot2.util.tokenizer.parser.StringTypedToken}
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