
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ShortArrayList;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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
     */
    @Attribute
    @Label("Document fields")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public List<String> documentFields = Arrays.asList(
        Document.TITLE, Document.SUMMARY
    );

    /**
     * Token images.
     */
    private ArrayList<char []> images;

    /**
     * An array of token types.
     *
     * @see ITokenizer
     */
    private ShortArrayList tokenTypes;

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
    public void tokenize(Stream<Document> documents, PreprocessingContext context, ITokenizer ts)
    {
        // Fields to tokenize
        final String [] fieldNames = documentFields.toArray(new String [documentFields.size()]);

        if (fieldNames.length > 8)
        {
            throw new ProcessingException("The maximum number of tokenized fields is 8.");
        }

        // Prepare arrays
        images = new ArrayList<>();
        tokenTypes = new ShortArrayList();
        documentIndices = new IntArrayList();
        fieldIndices = new ByteArrayList();

        AtomicInteger documentIndex = new AtomicInteger();
        final MutableCharArray wrapper = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);

        documents.forEachOrdered(doc -> {
            if (documentIndex.get() > 0) {
                addDocumentSeparator();
            }

            boolean hadTokens = false;
            for (int i = 0; i < fieldNames.length; i++)
            {
                final byte fieldIndex = (byte) i;
                final String fieldName = fieldNames[i];
                final String fieldValue = doc.getField(fieldName);

                if (!StringUtils.isNullOrEmpty(fieldValue))
                {
                    try
                    {
                        short tokenType;

                        ts.reset(new StringReader(fieldValue));
                        if ((tokenType = ts.nextToken()) != ITokenizer.TT_EOF)
                        {
                            if (hadTokens) {
                                addFieldSeparator(documentIndex.get());
                            }

                            do
                            {
                                ts.setTermBuffer(wrapper);
                                add(documentIndex.get(), fieldIndex, context.intern(wrapper), tokenType);
                            } while ( (tokenType = ts.nextToken()) != ITokenizer.TT_EOF);
                            hadTokens = true;
                        }
                    }
                    catch (IOException e)
                    {
                        // Not possible (StringReader above)?
                        throw ExceptionUtils.wrapAsRuntimeException(e);
                    }
                }
            }

            documentIndex.incrementAndGet();
        });

        addTerminator();

        // Save results in the PreprocessingContext
        context.documents = documentIndex.get();
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

    /**
     * Adds a special terminating token required at the very end of all documents.
     */
    void addTerminator()
    {
        add(-1, (byte) -1, null, ITokenizer.TF_TERMINATOR);
    }

    /**
     * Adds a document separator to the lists.
     */
    void addDocumentSeparator()
    {
        add(-1, (byte) -1, null, ITokenizer.TF_SEPARATOR_DOCUMENT);
    }

    /**
     * Adds a field separator to the lists.
     */
    void addFieldSeparator(int documentIndex)
    {
        add(documentIndex, (byte) -1, null, ITokenizer.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds custom token code to the sequence. May be used to add separator constants.
     */
    void add(int documentIndex, byte fieldIndex, char [] image, short tokenTypeCode)
    {
        documentIndices.add(documentIndex);
        fieldIndices.add(fieldIndex);
        images.add(image);
        tokenTypes.add(tokenTypeCode);
    }
}
