
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

package org.carrot2.text.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Init;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ShortArrayList;
import org.carrot2.shaded.guava.common.collect.Lists;

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
    @Init
    @Input
    @Attribute
    @Label("Document fields")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
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
    public void tokenize(PreprocessingContext context)
    {
        // Documents to tokenize
        final List<Document> documents = context.documents;
        
        // Fields to tokenize
        final String [] fieldNames = documentFields.toArray(new String [documentFields.size()]); 

        if (fieldNames.length > 8)
        {
            throw new ProcessingException("Maximum number of tokenized fields is 8.");
        }

        // Prepare arrays
        images = Lists.newArrayList();
        tokenTypes = new ShortArrayList();
        documentIndices = new IntArrayList();
        fieldIndices = new ByteArrayList();

        final Iterator<Document> docIterator = documents.iterator();
        int documentIndex = 0;
        final ITokenizer ts = context.language.getTokenizer();
        final MutableCharArray wrapper = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);

        while (docIterator.hasNext())
        {
            final Document doc = docIterator.next();

            boolean hadTokens = false;
            for (int i = 0; i < fieldNames.length; i++)
            {
                final byte fieldIndex = (byte) i;
                final String fieldName = fieldNames[i];
                final String fieldValue = doc.getField(fieldName);

                if (!StringUtils.isEmpty(fieldValue))
                {
                    try
                    {
                        short tokenType;

                        ts.reset(new StringReader(fieldValue));
                        if ((tokenType = ts.nextToken()) != ITokenizer.TT_EOF)
                        {
                            if (hadTokens) addFieldSeparator(documentIndex);
                            do
                            {
                                ts.setTermBuffer(wrapper);
                                add(documentIndex, fieldIndex, context.intern(wrapper), tokenType);
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
     * Adds a sentence separator to the lists.
     */
    void addSentenceSeparator(int documentIndex, byte fieldIndex)
    {
        add(documentIndex, fieldIndex, null, ITokenizer.TF_SEPARATOR_FIELD);
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
