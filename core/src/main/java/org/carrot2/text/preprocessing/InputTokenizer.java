/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;
import org.carrot2.clustering.Document;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.text.preprocessing.PreprocessingContext.AllTokens;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.MutableCharArray;
import org.carrot2.util.StringUtils;

/**
 * Performs tokenization of documents.
 *
 * <p>This class saves the following results to the {@link PreprocessingContext}:
 *
 * <ul>
 *   <li>{@link AllTokens#image}
 *   <li>{@link AllTokens#documentIndex}
 *   <li>{@link AllTokens#fieldIndex}
 *   <li>{@link AllTokens#type}
 * </ul>
 */
final class InputTokenizer {
  /** Token images. */
  private ArrayList<char[]> images;

  /** An array of token types. */
  private ShortArrayList tokenTypes;

  /** An array of document indexes. */
  private IntArrayList documentIndices;

  /**
   * An array of field indexes.
   *
   * @see AllFields
   */
  private ByteArrayList fieldIndices;

  private static class FieldValue {
    String field;
    String value;

    public FieldValue(String fieldName, String fieldValue) {
      this.field = fieldName;
      this.value = fieldValue;
    }
  }

  /** Performs tokenization and saves the results to the <code>context</code>. */
  public void tokenize(PreprocessingContext context, Stream<? extends Document> docStream) {
    images = new ArrayList<>();
    tokenTypes = new ShortArrayList();
    documentIndices = new IntArrayList();
    fieldIndices = new ByteArrayList();

    final Tokenizer ts = context.languageComponents.get(Tokenizer.class);
    final MutableCharArray wrapper = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);

    HashMap<String, Integer> fieldIndexes = new HashMap<>();
    ArrayList<FieldValue> fields = new ArrayList<>();

    IntCursor docCount = new IntCursor();
    docStream.forEachOrdered(
        (doc) -> {
          int documentIndex = docCount.value;
          if (documentIndex > 0) {
            addDocumentSeparator();
          }

          fields.clear();
          doc.visitFields(
              (fieldName, fieldValue) -> {
                if (!StringUtils.isNullOrEmpty(fieldValue)) {
                  fields.add(new FieldValue(fieldName, fieldValue));
                }
              });

          boolean hadTokens = false;
          for (FieldValue fv : fields) {
            final int fieldIndex =
                fieldIndexes.computeIfAbsent(fv.field, (k) -> fieldIndexes.size());
            if (fieldIndex > Byte.MAX_VALUE) {
              throw new RuntimeException("Too many fields (>" + fieldIndex + ")");
            }
            final String fieldValue = fv.value;

            if (!StringUtils.isNullOrEmpty(fieldValue)) {
              try {
                short tokenType;

                ts.reset(new StringReader(fieldValue));
                if ((tokenType = ts.nextToken()) != Tokenizer.TT_EOF) {
                  if (hadTokens) addFieldSeparator(documentIndex);
                  do {
                    ts.setTermBuffer(wrapper);
                    add(documentIndex, (byte) fieldIndex, context.intern(wrapper), tokenType);
                  } while ((tokenType = ts.nextToken()) != Tokenizer.TT_EOF);
                  hadTokens = true;
                }
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          }

          docCount.value++;
        });

    addTerminator();

    String[] fieldNames = new String[fieldIndexes.size()];
    fieldIndexes.forEach((field, index) -> fieldNames[index] = field);

    // Save results in the PreprocessingContext
    context.documentCount = docCount.value;
    context.allTokens.documentIndex = documentIndices.toArray();
    context.allTokens.fieldIndex = fieldIndices.toArray();
    context.allTokens.image = images.toArray(new char[images.size()][]);
    context.allTokens.type = tokenTypes.toArray();
    context.allFields.name = fieldNames;

    // Clean up
    images = null;
    fieldIndices = null;
    tokenTypes = null;
    documentIndices = null;
  }

  /** Adds a special terminating token required at the very end of all documents. */
  void addTerminator() {
    add(-1, (byte) -1, null, Tokenizer.TF_TERMINATOR);
  }

  /** Adds a document separator to the lists. */
  void addDocumentSeparator() {
    add(-1, (byte) -1, null, Tokenizer.TF_SEPARATOR_DOCUMENT);
  }

  /** Adds a field separator to the lists. */
  void addFieldSeparator(int documentIndex) {
    add(documentIndex, (byte) -1, null, Tokenizer.TF_SEPARATOR_FIELD);
  }

  /** Adds custom token code to the sequence. May be used to add separator constants. */
  void add(int documentIndex, byte fieldIndex, char[] image, short tokenTypeCode) {
    documentIndices.add(documentIndex);
    fieldIndices.add(fieldIndex);
    images.add(image);
    tokenTypes.add(tokenTypeCode);
  }
}
