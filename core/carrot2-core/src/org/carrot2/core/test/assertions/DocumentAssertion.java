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
        assertThat(actualDocument.getId()).isEqualTo(expectedDocument.getId());
        assertThat(actualDocument.getFields()).isEqualTo(expectedDocument.getFields());
        return this;
    }
}
