package org.carrot2.text.preprocessing.pipeline;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Init;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllFields;
import org.carrot2.text.util.CharArrayComparators;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.LinkedFrequencyList;
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
import com.carrotsearch.hppc.IntIntOpenHashMap;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectIntOpenHashMap;
import com.carrotsearch.hppc.ObjectOpenHashSet;
import com.carrotsearch.hppc.ShortArrayList;
import com.google.common.collect.Lists;

@Bindable
class LuceneAnalyzerPreprocessor
{
    /**
     * Case normalizer used by the algorithm, contains bindable attributes.
     */
    public final CaseNormalizer caseNormalizer = new CaseNormalizer();

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
     * Raw token images.
     */
    private ArrayList<char []> images;

    /**
     * Processed images (possibly normalized and stemmed).
     */
    private ArrayList<char []> processedImages;

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
     * Stems of all query tokens.
     */
    private ObjectOpenHashSet<char[]> queryStems = ObjectOpenHashSet.newInstance();

    /** */
    public void preprocessDocuments(PreprocessingContext context, Analyzer analyzer)
    {
        // Documents to tokenize
        final List<Document> documents = context.documents;

        // Fields to tokenize
        final String [] fieldNames = documentFields.toArray(new String [documentFields.size()]); 

        if (fieldNames.length > 8)
            throw new ProcessingException("Maximum number of tokenized fields is 8.");

        // Prepare output arrays
        images = Lists.newArrayList();
        processedImages = Lists.newArrayList();
        tokenTypes = new ShortArrayList();
        documentIndices = new IntArrayList();
        fieldIndices = new ByteArrayList();

        final Iterator<Document> docIterator = documents.iterator();
        int documentIndex = 0;

        final MutableCharArray tokenImage = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);
        final MutableCharArray rawImage = new MutableCharArray(CharArrayUtils.EMPTY_ARRAY);

        // Preprocess the query first to be able to mark tokens.
        preprocessValue(context, analyzer, -1, tokenImage, rawImage, (byte) -1, "query", context.query);
        for (char[] stemmedImage : processedImages) {
            queryStems.add(stemmedImage);
        }
        images.clear();
        processedImages.clear();
        tokenTypes.clear();
        documentIndices.clear();
        fieldIndices.clear();

        // Preprocess documents.
        while (docIterator.hasNext())
        {
            final Document doc = docIterator.next();

            for (int i = 0; i < fieldNames.length; i++)
            {
                final byte fieldIndex = (byte) i;
                final String fieldName = fieldNames[i];
                final String fieldValue = doc.getField(fieldName);

                preprocessValue(context, analyzer, documentIndex, tokenImage, rawImage,
                    fieldIndex, fieldName, fieldValue);
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

        // Store temporary stem images.
        context.allStems.image = processedImages.toArray(new char [processedImages.size()][]);

        // Clean up
        images = null;
        fieldIndices = null;
        tokenTypes = null;
        documentIndices = null;
        processedImages = null;
    }

    /**
     * Preprocess a single value.
     */
    private void preprocessValue(PreprocessingContext context, Analyzer analyzer,
        int documentIndex, final MutableCharArray tokenImage,
        final MutableCharArray rawImage, final byte fieldIndex,
        final String fieldName, final String fieldValue)
    {
        if (org.apache.commons.lang.StringUtils.isEmpty(fieldValue))
        {
            return;
        }

        try
        {
            final TokenStream ts = analyzer.reusableTokenStream(fieldName, new StringReader(fieldValue));
            final CharTermAttribute charTerm = ts.getAttribute(CharTermAttribute.class);
            final OffsetAttribute offset = ts.getAttribute(OffsetAttribute.class);
            final PositionIncrementAttribute posIncrement = ts.getAttribute(PositionIncrementAttribute.class);
            final CommonWordAttribute commonWord = ts.addAttribute(CommonWordAttribute.class);
            final TokenTypeAttribute tokenTypeAtt = ts.getAttribute(TokenTypeAttribute.class);

            boolean firstFieldToken = true;
            while (ts.incrementToken()) {
                // TODO what about pos. increment is > 1?
                if (posIncrement.getPositionIncrement() == 0) {
                    continue;
                }

                if (firstFieldToken)
                {
                    final int lastDocIndex = documentIndices.size() - 1;
                    if (lastDocIndex >= 0 && documentIndices.get(lastDocIndex) == documentIndex) {
                        addFieldSeparator(documentIndex);
                    }
                    firstFieldToken = false;
                }

                // Get the processed token image.
                tokenImage.reset(charTerm);

                // Reconstruct the 'raw' token image based on position/ offsets.
                rawImage.reset(fieldValue.substring(offset.startOffset(), offset.endOffset()));

                int tokenType = tokenTypeAtt.getType();
                if (commonWord.isCommon())
                {
                    tokenType |= ITokenizer.TF_COMMON_WORD;
                }

                add(documentIndex, 
                    fieldIndex, 
                    context.intern(rawImage),
                    context.intern(tokenImage),
                    (short) tokenType);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Adds a special terminating token required at the very end of all documents.
     */
    void addTerminator()
    {
        add(-1, (byte) -1, null, null, ITokenizer.TF_TERMINATOR);
    }

    /**
     * Adds a document separator to the lists.
     */
    void addDocumentSeparator()
    {
        add(-1, (byte) -1, null, null, ITokenizer.TF_SEPARATOR_DOCUMENT);
    }

    /**
     * Adds a field separator to the lists.
     */
    void addFieldSeparator(int documentIndex)
    {
        add(documentIndex, (byte) -1, null, null, ITokenizer.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds a sentence separator to the lists.
     */
    void addSentenceSeparator(int documentIndex, byte fieldIndex)
    {
        add(documentIndex, fieldIndex, null, null, ITokenizer.TF_SEPARATOR_FIELD);
    }

    /**
     * Adds custom token code to the sequence. May be used to add separator constants.
     */
    void add(int documentIndex, byte fieldIndex, char [] image, char [] processedImage, short tokenTypeCode)
    {
        documentIndices.add(documentIndex);
        fieldIndices.add(fieldIndex);
        images.add(image);
        processedImages.add(processedImage);
        tokenTypes.add(tokenTypeCode);
    }

    /**
     * Fills in {@link PreprocessingContext#allStems} with data corresponding to stems.
     */
    void fillStemData(PreprocessingContext ctx)
    {
        // Stem images for all tokens in the input.
        char[][] stemImages = ctx.allStems.image;
        int [] wordIndexes = ctx.allTokens.wordIndex;
        int [] stemIndexes = ctx.allWords.stemIndex = new int [ctx.allWords.image.length];
        Arrays.fill(stemIndexes, -1);

        // Data structures to be filled in (ctx.allStems)
        ObjectArrayList<char[]> image = ObjectArrayList.newInstance();
        IntArrayList mostFrequentOriginalWordIndex = IntArrayList.newInstance();
        IntArrayList tf = IntArrayList.newInstance();
        ByteArrayList fieldIndices = ByteArrayList.newInstance();
        
        // stem images will be interned so we can use hashcode/equals here.
        ObjectIntOpenHashMap<char[]> stemImageMap = ObjectIntOpenHashMap.newInstance();

        // In the first pass collect information about stems and point from words to stems.
        // leave tf/tfByDocument accounting for later.
        for (int i = 0; i < wordIndexes.length; i++)
        {
            final int wordIndex = wordIndexes[i];
            if (wordIndex < 0)
            {
                continue;
            }

            final char [] stemImage = stemImages[i];

            if (stemIndexes[wordIndex] >= 0) {
                /*
                if (!sameStemImages(image.get(stemIndexes[wordIndex]), stemImage)) {
                    Logger.getLogger("").warning("Token: " + new String(ctx.allTokens.image[i]));
                }
                */
                continue;
            }

            final int stemIndex;
            if (stemImageMap.containsKey(stemImage)) {
                stemIndex = stemImageMap.lget();
            } else {
                stemIndex = image.size();
                stemImageMap.put(stemImage, stemIndex);

                // Create a new stem entry.
                image.add(stemImage);
                mostFrequentOriginalWordIndex.add(wordIndex);
                tf.add(0);
                fieldIndices.add((byte) 0);                
            }
            stemIndexes[wordIndex] = stemIndex;
        }

        // Run post-accounting.
        for (int i = 0; i < stemIndexes.length; i++)
        {
            final int stemIndex = stemIndexes[i];
            if (stemIndex != -1)
            {
                // counters and bitfields.
                tf.buffer[stemIndex] += ctx.allWords.tf[i];
                fieldIndices.buffer[stemIndex] |= ctx.allWords.fieldIndices[i];

                // update most frequent original form if more frequent than current.
                if (isBetterRepresentative(ctx, mostFrequentOriginalWordIndex.get(stemIndex), i))
                {
                    mostFrequentOriginalWordIndex.set(stemIndex, i);
                }

                // Propagate part-of-query flag.
                if (queryStems.contains(image.get(stemIndex)))
                {
                    ctx.allWords.type[i] |= ITokenizer.TF_QUERY_WORD;
                }
            }
        }

        // update tfByDocument directly from the input tokens list
        // because we want documents to come in order.
        final LinkedFrequencyList tfByDocumentFreqs = new LinkedFrequencyList();
        final IntIntOpenHashMap stemEntries = IntIntOpenHashMap.newInstance(); 
        for (int i = wordIndexes.length; --i >= 0;)
        {
            final int wordIndex = wordIndexes[i];
            if (wordIndex >= 0)
            {
                final int stemIndex = stemIndexes[wordIndex];
                int entry = stemEntries.containsKey(stemIndex)
                    ? stemEntries.lget()
                    : LinkedFrequencyList.NO_NEXT;

                int newEntry = tfByDocumentFreqs.addOne(entry, ctx.allTokens.documentIndex[i]);
                if (newEntry != entry)
                {
                    stemEntries.put(stemIndex, newEntry);
                }
            }
        }

        ObjectArrayList<int[]> tfByDocument = ObjectArrayList.newInstance();
        IntStack tmpStack = IntStack.newInstance();
        for (int i = 0; i < image.size(); i++)
        {
            tmpStack.clear();
            tfByDocumentFreqs.collect(tmpStack, stemEntries.get(i));
            tfByDocument.add(tmpStack.toArray());
        }

        ctx.allStems.image = image.toArray(char[].class);
        ctx.allStems.mostFrequentOriginalWordIndex = mostFrequentOriginalWordIndex.toArray();
        ctx.allStems.tf = tf.toArray();
        ctx.allStems.tfByDocument = tfByDocument.toArray(int[].class);
        ctx.allStems.fieldIndices = fieldIndices.toArray();
    }

    /**
     * Pick a better representative token for a word consistently, with no 
     * dependency on the order if token images. 
     */
    private static boolean isBetterRepresentative(
        PreprocessingContext preprocessingContext,
        int mostFrequentWordIndex, int candidateWordIndex)
    {
        if (mostFrequentWordIndex == candidateWordIndex) {
            return false;
        }

        final int [] wordTfArray = preprocessingContext.allWords.tf;
        
        // Check frequency first.
        if (wordTfArray[candidateWordIndex] > wordTfArray[mostFrequentWordIndex])
            return true;

        // If equal frequency, more compact form wins.
        if (wordTfArray[candidateWordIndex] == wordTfArray[mostFrequentWordIndex])
        {
            final char [][] wordImages = preprocessingContext.allWords.image;
            if (wordImages[candidateWordIndex].length < wordImages[mostFrequentWordIndex].length)
                return true;
            
            // If equal frequency and length, smaller lexicographic image wins.
            return CharArrayComparators.FAST_CHAR_ARRAY_COMPARATOR.compare(
                wordImages[candidateWordIndex],
                wordImages[mostFrequentWordIndex]) < 0;
        }
        return false;
    }    

    /**
     * Compare stem images and emit a warning if different.
     */
    @SuppressWarnings("unused")
    private static boolean sameStemImages(char [] si1, char [] si2)
    {
        if (!Arrays.equals(si1, si2)) {
            Logger.getLogger("").warning(
                "Different stem images for identical token: "
                    + new String(si1) + ", "
                    + new String(si2));
            return false;
        }
        return true;
    }
}
