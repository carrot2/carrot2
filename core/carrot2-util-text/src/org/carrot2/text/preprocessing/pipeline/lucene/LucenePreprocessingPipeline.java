package org.carrot2.text.preprocessing.pipeline.lucene;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.IStemmer;
import org.carrot2.text.linguistic.IStemmerFactory;
import org.carrot2.text.linguistic.ITokenizerFactory;
import org.carrot2.text.linguistic.LanguageModel;
import org.carrot2.text.linguistic.LexicalDataLoader;
import org.carrot2.text.preprocessing.CaseNormalizer;
import org.carrot2.text.preprocessing.DocumentAssigner;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.PhraseExtractor;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * A {@link IPreprocessingPipeline} based on tokenizers and filtering 
 * components from the Apache Lucene project.
 * 
 * @see "http://lucene.apache.org"
 */
@Bindable(inherit = LexicalDataLoader.class)
public class LucenePreprocessingPipeline implements IPreprocessingPipeline
{
    /** A dummy unusable {@link IStemmerFactory}. */
    private final static IStemmerFactory NOSTEMMER = new IStemmerFactory() {
        @Override
        public IStemmer getStemmer(LanguageCode languageCode) { throw new RuntimeException("Not available."); }
    };

    /** A dummy unusable {@link ITokenizerFactory}. */
    private final static ITokenizerFactory NOTOKENIZER = new ITokenizerFactory() {
        @Override
        public ITokenizer getTokenizer(LanguageCode languageCode) { throw new RuntimeException("Not available."); }
    };

    /**
     * Lexical data factory. Creates the lexical data to be used by the clustering
     * algorithm, including stop word and stop label dictionaries.
     */
    @Input
    @Init
    @Processing
    @Internal
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.PREPROCESSING)
    public ILexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

    /**
     * A provider of Lucene analyzers to do token stream preprocessing. 
     * The TokenStream components in the chain should delimit
     * sentence boundaries (return boundary tokens) and mark stop words. See
     * the source code of {@link DefaultAnalyzerProvider} for
     * an example. 
     */
    @Init
    @Processing
    @Input 
    @Internal
    @Attribute(key = "analyzer")
    @ImplementingClasses(classes = {}, strict = false)
    public IAnalyzerProvider analyzerProvider = new DefaultAnalyzerProvider();

    /** */
    private final LuceneAnalyzerPreprocessor preprocessor = new LuceneAnalyzerPreprocessor();
    
    /**
     * Case normalizer used by the algorithm, contains bindable attributes.
     */
    public final CaseNormalizer caseNormalizer = new CaseNormalizer();

    /**
     * Phrase extractor used by the algorithm, contains bindable attributes.
     */
    public final PhraseExtractor phraseExtractor = new PhraseExtractor();

    /**
     * Label filter processor used by the algorithm, contains bindable attributes.
     */
    public final LabelFilterProcessor labelFilterProcessor = new LabelFilterProcessor();

    /**
     * Document assigner used by the algorithm, contains bindable attributes.
     */
    public final DocumentAssigner documentAssigner = new DocumentAssigner();

    @Override
    public PreprocessingContext preprocess(List<Document> documents, String query, LanguageCode language, ContextRequired contextRequired)
    {
        // Pick an analyzer based on the language.
        final Analyzer analyzer = analyzerProvider.getAnalyzerFor(language, lexicalDataFactory);

        // Process the input.
        final PreprocessingContext context = new PreprocessingContext(
            LanguageModel.create(language,
                NOSTEMMER, 
                NOTOKENIZER, 
                lexicalDataFactory), documents, query);

        // Finalize processing steps.
        preprocessor.preprocessDocuments(context, analyzer);
        caseNormalizer.normalize(context);
        preprocessor.fillStemData(context);
        
        if (contextRequired == ContextRequired.COMPLETE)
        {
            phraseExtractor.extractPhrases(context);
            labelFilterProcessor.process(context);
            documentAssigner.assign(context);
        }

        context.preprocessingFinished();
        return context;
    }
}
