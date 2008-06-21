package org.carrot2.text.preprocessing;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.*;
import org.carrot2.core.Document;
import org.carrot2.text.analysis.TokenType;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.Pair;

import bak.pcj.list.ByteArrayList;
import bak.pcj.list.IntArrayList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of {@link PreprocessingTasks#TOKENIZE}.
 */
final class Tokenizer
{
    /**
     * Token images.
     */
    private final ArrayList<char []> images = Lists.newArrayList();

    /**
     * An array of token types.
     * 
     * @see TokenType
     */
    private final IntArrayList tokenTypes = new IntArrayList();

    /**
     * An array of document indexes.
     */
    private final IntArrayList documentIndices = new IntArrayList();

    /**
     * An array of field indexes.
     * 
     * @see AllFields
     */
    private final ByteArrayList fieldIndices = new ByteArrayList();

    /**
     * Field names corresponding to {@link #fieldIndices}.
     */
    private String [] fieldNames;

    /**
     * Performs tokenization and saves the results to the <code>context</code>.
     */
    public void tokenize(PreprocessingContext context, Collection<Document> documents,
        Collection<String> documentFields, Analyzer analyzer)
    {
        // Map field names to their indices in AllFields
        final Map<String, Byte> fieldNameToIndex = createFieldNameToIndexMap(context,
            documentFields);
        fieldNames = fieldNameToIndex.keySet().toArray(
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

                Token t = null;
                if (!StringUtils.isEmpty(fieldValue))
                {
                    try
                    {
                        final TokenStream ts = analyzer.reusableTokenStream(null,
                            new StringReader(fieldValue));

                        while ((t = ts.next(t)) != null)
                        {
                            add(documentIndex, fieldNameToIndex.get(fieldName), t);
                        }
                    }
                    catch (IOException e)
                    {
                        // Not possible (StringReader above)?
                        throw ExceptionUtils.wrapAs(RuntimeException.class, e);
                    }
                    catch (ClassCastException e)
                    {
                        throw new RuntimeException("The analyzer must provide "
                            + TokenType.class.getName() + " instances as payload.");
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
     * {@link TokenType} payload.
     */
    void add(int documentIndex, byte fieldIndex, Token token)
    {
        final TokenType type = (TokenType) token.getPayload();
        final char [] buffer = new char [token.termLength()];
        System.arraycopy(token.termBuffer(), 0, buffer, 0, token.termLength());
        add(documentIndex, fieldIndex, buffer, type.getRawFlags());
    }

    /**
     * Adds a special terminating token required at the very end of all documents.
     */
    void addTerminator()
    {
        add(-1, (byte) -1, null, TokenType.TF_TERMINATOR);
    }

    /**
     * Adds a document separator to the lists.
     */
    void addDocumentSeparator()
    {
        add(-1, (byte) -1, null, TokenType.TF_SEPARATOR_DOCUMENT);
    }

    /**
     * Adds a field separator to the lists.
     */
    void addFieldSeparator(int documentIndex)
    {
        add(documentIndex, (byte) -1, null, TokenType.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds a sentence separator to the lists.
     */
    void addSentenceSeparator(int documentIndex, byte fieldIndex)
    {
        add(documentIndex, fieldIndex, null, TokenType.TF_SEPARATOR_FIELD);
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
