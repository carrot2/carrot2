
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
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

/**
 * Assertions on lists of {@link Document}s.
 */
public class DocumentListAssertion extends GenericListAssertion<DocumentListAssertion, Document>
{
    DocumentListAssertion(List<Document> actualDocumentList)
    {
        super(DocumentListAssertion.class, actualDocumentList);
    }

    /**
     * Asserts that the document list is equivalent to the provided document list. Two
     * document lists are equivalent if they have the same size and if the documents on
     * the corresponding positions are equivalent (see
     * {@link DocumentAssertion#isEquivalentTo(Document)}.
     * 
     * @param expectedDocumentList the expected document list
     * @return this assertion for convenience
     */
    public DocumentListAssertion isEquivalentTo(List<Document> expectedDocumentList)
    {
        assertThat(actual).hasSize(expectedDocumentList.size());
        for (int i = 0; i < actual.size(); i++)
        {
            assertThat(actual.get(i)).as(description() + ", document: " + i)
                .isEquivalentTo(expectedDocumentList.get(i));
        }
        return this;
    }

    /**
     * Asserts that the document list contains all the provided documents. Containment is
     * defined as in {@link List#contains(Object)}.
     * 
     * @param documents the documents that the tested document list is to contain
     * @return this assertion for convenience
     */
    public DocumentListAssertion contains(List<Document> documents)
    {
        for (int i = 0; i < documents.size(); i++)
        {
            assertThat(actual.contains(documents.get(i))).as(
                description() + ", contains document: " + i + ", title: "
                    + documents.get(i).getTitle()).isTrue();
        }
        return this;
    }
}
