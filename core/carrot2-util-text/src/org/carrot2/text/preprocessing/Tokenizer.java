/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.carrot2.core.Document;
import org.carrot2.core.attribute.Init;
import org.carrot2.text.analysis.ITokenType;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;
import org.carrot2.util.attribute.*;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Performs tokenization of documents.
 * <p>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllTokens#image}</li>
 * <li>{@link AllTokens#documentIndex}</li>
 * <li>{@link AllTokens#fieldIndex}</li>
 * <li>{@link AllTokens#type}</li>
 * </ul>
 */
@Bindable(prefix = "Tokenizer")
public final class Tokenizer
{
    /**
     * Textual fields of documents that should be tokenized and parsed for clustering.
     * 
     * @level Advanced
     * @group Preprocessing
     * @label Document fields
     */
    @Init
    @Input
    @Attribute
    public Collection<String> documentFields = Arrays.asList(new String []
    {
        Document.TITLE, Document.SUMMARY
    });

    /**
     * Token images.
     */
    private ArrayList<char []> images;

    /**
     * An array of token types.
     * 
     * @see ITokenType
     */
    private IntArrayList tokenTypes;

    /**
     * An array of document indexes.
     */
    private IntArrayList documentIndices;

    /**
     * An array of field indexes.
     * 
     * @see AllFields
     */
    private ByteArrayList fieldIndices;

    /**
     * Performs tokenization and saves the results to the <code>context</code>.
     */
    public void tokenize(PreprocessingContext context)
    {
        // Documents to tokenize
        final List<Document> documents = context.documents;

        // Prepare arrays
        images = Lists.newArrayList();
        tokenTypes = new IntArrayList();
        documentIndices = new IntArrayList();
        fieldIndices = new ByteArrayList();

        // Map field names to their indices in AllFields
        final Map<String, Byte> fieldNameToIndex = createFieldNameToIndexMap(context,
            documentFields);
        final String [] fieldNames = fieldNameToIndex.keySet().toArray(
            new String [fieldNameToIndex.keySet().size()]);

        final ArrayList<Pair<String, String>> nonEmptyFieldValues = Lists.newArrayList();

        final Iterator<Document> docIterator = documents.iterator();
        int documentIndex = 0;
        while (docIterator.hasNext())
        {
            final Document doc = docIterator.next();

            // Queue all non-empty document fields for this document.
            nonEmptyFieldValues.clear();
            for (String fieldName : documentFields)
            {
                final String fieldValue = doc.getField(fieldName);
                if (!StringUtils.isEmpty(fieldValue))
                {
                    nonEmptyFieldValues.add(new Pair<String, String>(fieldName,
                        fieldValue));
                }
            }

            for (Iterator<Pair<String, String>> it = nonEmptyFieldValues.iterator(); it
                .hasNext();)
            {
                final Pair<String, String> fieldEntry = it.next();
                final String fieldName = fieldEntry.objectA;
                final String fieldValue = fieldEntry.objectB;

                if (!StringUtils.isEmpty(fieldValue))
                {
                    try
                    {
                        final TokenStream ts = context.language.getTokenizer()
                            .tokenStream(null, new StringReader(fieldValue));

                        while (ts.incrementToken())
                        {
                            add(documentIndex, fieldNameToIndex.get(fieldName), ts
                                .getAttribute(TermAttribute.class), ts
                                .getAttribute(PayloadAttribute.class));
                        }
                    }
                    catch (IOException e)
                    {
                        // Not possible (StringReader above)?
                        throw ExceptionUtils.wrapAsRuntimeException(e);
                    }
                    catch (ClassCastException e)
                    {
                        throw new RuntimeException("The analyzer must provide "
                            + ITokenType.class.getName() + " instances as payload.");
                    }

                    if (it.hasNext())
                    {
                        addFieldSeparator(documentIndex);
                    }
                }
            }

            if (docIterator.hasNext())
            {
                addDocumentSeparator();
            }

            documentIndex++;
        }

        addTerminator();

        // Save results in the PreprocessingContext
        context.allTokens.documentIndex = documentIndices.toArray();
        context.allTokens.fieldIndex = fieldIndices.toArray();
        context.allTokens.image = images.toArray(new char [images.size()] []);
        context.allTokens.type = tokenTypes.toArray();
        context.allFields.name = fieldNames;

        // Clean up
        images = null;
        fieldIndices = null;
        tokenTypes = null;
        documentIndices = null;
    }

    private Map<String, Byte> createFieldNameToIndexMap(PreprocessingContext context,
        Collection<String> documentFields)
    {
        final Map<String, Byte> result = Maps.newLinkedHashMap();

        byte fieldCode = 0;
        for (String docmentField : documentFields)
        {
            result.put(docmentField, fieldCode++);
        }

        return result;
    }

    /**
     * Add the token's code to the list. The <code>token</code> must carry
     * {@link ITokenType} payload.
     */
    void add(int documentIndex, byte fieldIndex, TermAttribute term,
        PayloadAttribute payload)
    {
        final ITokenType type = (ITokenType) payload.getPayload();
        final int termLength = term.termLength();
        final char [] buffer = new char [termLength];
        System.arraycopy(term.termBuffer(), 0, buffer, 0, termLength);
        add(documentIndex, fieldIndex, buffer, type.getRawFlags());
    }

    /**
     * Adds a special terminating token required at the very end of all documents.
     */
    void addTerminator()
    {
        add(-1, (byte) -1, null, ITokenType.TF_TERMINATOR);
    }

    /**
     * Adds a document separator to the lists.
     */
    void addDocumentSeparator()
    {
        add(-1, (byte) -1, null, ITokenType.TF_SEPARATOR_DOCUMENT);
    }

    /**
     * Adds a field separator to the lists.
     */
    void addFieldSeparator(int documentIndex)
    {
        add(documentIndex, (byte) -1, null, ITokenType.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds a sentence separator to the lists.
     */
    void addSentenceSeparator(int documentIndex, byte fieldIndex)
    {
        add(documentIndex, fieldIndex, null, ITokenType.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds custom token code to the sequence. May be used to add separator constants.
     */
    void add(int documentIndex, byte fieldIndex, char [] image, int tokenTypeCode)
    {
        documentIndices.add(documentIndex);
        fieldIndices.add(fieldIndex);
        images.add(image);
        tokenTypes.add(tokenTypeCode);
    }
}
