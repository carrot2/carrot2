
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

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Document;
import org.fest.assertions.AssertExtension;
import org.fest.assertions.Assertions;

/**
 * Assertions on lists of {@link Document}s.
 */
public class DocumentListAssertion implements AssertExtension
{
    /** The actual list of documents */
    private final List<Document> actualDocumentList;

    /** Description for this assertion */
    private String description;

    DocumentListAssertion(List<Document> actualDocumentList)
    {
        this.actualDocumentList = actualDocumentList;
    }

    /**
     * Asserts that the document cluster list is equivalent to the provided document list.
     * Two document lists are equivalent if they have the same size and if the documents
     * on the corresponding positions are equivalent (see
     * {@link DocumentAssertion#isEquivalentTo(Document)}.
     * 
     * @param expectedDocumentList the expected document list
     * @return this assertion for convenience
     */
    public DocumentListAssertion isEquivalentTo(List<Document> expectedDocumentList)
    {
        assertThat(actualDocumentList).hasSize(expectedDocumentList.size());
        for (int i = 0; i < actualDocumentList.size(); i++)
        {
            assertThat(actualDocumentList.get(i)).as(description + ", document: " + i).isEquivalentTo(
                expectedDocumentList.get(i));
        }
        return this;
    }

    /**
     * Asserts that the document list has the provided size.
     * 
     * @param expectedSize the expected list size
     * @return this assertion for convenience
     */
    public DocumentListAssertion hasSize(int expectedSize)
    {
        Assertions.assertThat(actualDocumentList).as(description).hasSize(expectedSize);
        return this;
    }

    /**
     * Provides description for this assertion.
     */
    public DocumentListAssertion as(String description)
    {
        this.description = description;
        return this;
    }
}
