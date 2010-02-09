
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Document;
import org.fest.assertions.AssertExtension;

/**
 * Assertions on {@link Document}s.
 */
public class DocumentAssertion implements AssertExtension
{
    /** The actual documents */
    private final Document actualDocument;

    /** Assert description */
    private String description;

    DocumentAssertion(Document actualDocument)
    {
        this.actualDocument = actualDocument;
    }

    /**
     * Asserts that the document is equivalent to the provided document. Two documents are
     * equivalent if their {@link Document#getId()} and {@link Document#getFields()} are
     * equal.
     * 
     * @param expectedDocument the expected document
     * @return this assertion for convenience
     */
    public DocumentAssertion isEquivalentTo(Document expectedDocument)
    {
        assertThat((Object) actualDocument.getId()).as(description + ", id").isEqualTo(
            expectedDocument.getId());
        assertThat(actualDocument.getFields()).as(description).isEqualTo(
            expectedDocument.getFields());
        return this;
    }

    /**
     * Provides description for this assertion.
     */
    public DocumentAssertion as(String description)
    {
        this.description = description;
        return this;
    }
}
