/**
 * 
 * @author chilang
 * Created 2004-01-13, 14:44:35.
 */
package com.chilang.carrot.filter.cluster.rough;

import com.chilang.carrot.filter.cluster.rough.data.IRContext;
import com.chilang.carrot.filter.cluster.rough.data.SimpleTermExtractor;
import com.chilang.carrot.filter.cluster.rough.data.TermExtractor;
import com.chilang.carrot.filter.cluster.rough.data.WebIRContext;
import com.chilang.carrot.filter.cluster.rough.filter.StopWordsSet;
import com.chilang.carrot.filter.cluster.rough.filter.TermFilter;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.PorterStemmer;
import com.chilang.carrot.filter.cluster.rough.filter.stemmer.Stemmer;
import com.chilang.carrot.filter.cluster.rough.transformer.Snippet2DocumentTransformer;
import com.chilang.carrot.filter.cluster.rough.trsm.RoughSpace;
import com.chilang.carrot.filter.cluster.rough.trsm.SnippetReader;
import com.chilang.carrot.filter.cluster.rough.trsm.TermRoughSpace;


/**
 * Factor for common used classes
 */
public class CommonFactory {

    private CommonFactory() {}

    public static Stemmer createDefaultStemmer() {
        return new PorterStemmer();
    }

    public static StopWordsSet createStopWordsSet() {
        return new StopWordsSet("stopwords/stopwords-en.txt");
    }

    /**
     * Create context from given data file
     * @param datafile
     * @return
     */
    public static IRContext createDefaultContext(String datafile) {
        SnippetReader reader = new SnippetReader(datafile);
        IRContext context = new WebIRContext(
                createStopWordsSet(),
                new Snippet2DocumentTransformer(
                        createSimpleTermExtractor(createDefaultStemmer(), createStopWordsSet())));


        context.setQuery(reader.getQuery());
        context.setDocuments(reader.getSnippets());
        return context;
    }

    public static TermExtractor createSimpleTermExtractor(Stemmer stemmer, TermFilter filter) {
        return new SimpleTermExtractor(stemmer, filter);
    }

//    public static IRContext createContextFromCACM(String filename) {
//        CACMFileReader fileReader = new CACMFileReader(filename);
//        return new WebIRContext(fileReader.getSnippets());
//    }

    public static RoughSpace createRoughSpace(int[][] documentTermFrequency, int cooccurenceThreshold, double inclusionThreshold) {
        return new TermRoughSpace(documentTermFrequency, cooccurenceThreshold, inclusionThreshold);
    }

}
