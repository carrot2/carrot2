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
            assertThat(actualDocumentList.get(i)).isEquivalentTo(
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
        Assertions.assertThat(actualDocumentList).hasSize(expectedSize);
        return this;
    }
}
