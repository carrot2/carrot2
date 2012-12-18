package org.carrot2.text.preprocessing.pipeline.lucene;

import static org.carrot2.util.resource.ResourceLookup.Location.CONTEXT_CLASS_LOADER;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.ja.JapaneseBaseFormFilter;
import org.apache.lucene.analysis.ja.JapaneseKatakanaStemFilter;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.util.Version;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.linguistic.DefaultLexicalDataFactory;
import org.carrot2.text.linguistic.ILexicalDataFactory;
import org.carrot2.text.linguistic.LexicalDataLoader;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.ResourceLookup;

/** 
 * Default implementation of {@link IAnalyzerProvider}.
 */
@Bindable(inherit = LexicalDataLoader.class)
public class DefaultAnalyzerProvider implements IAnalyzerProvider
{
    @Init
    @Processing
    @Input 
    @Internal
    @Attribute(key = "resource-lookup", inherit = true)
    @ImplementingClasses(classes = {}, strict = false)
    public ResourceLookup resourceLookup = new ResourceLookup(CONTEXT_CLASS_LOADER);

    @Processing
    @Input
    @Attribute(key = "reload-resources", inherit = true)
    public boolean reloadResources = false;

    /**
     * Pick analyzer and token stream for the given language.
     */
    public Analyzer getAnalyzerFor(LanguageCode language, ILexicalDataFactory lexicalDataFactory)
    {
        switch (language) {
            case ENGLISH:
                return defaultEnglishAnalyzer(lexicalDataFactory);
            case JAPANESE:
                return defaultJapaneseAnalyzer(lexicalDataFactory);
            default:
                throw new RuntimeException(this.getClass().getSimpleName() + 
                    " cannot be used with language: " + language);
        }
    }

    private Analyzer defaultEnglishAnalyzer(final ILexicalDataFactory lexicalDataFactory)
    {
        final class Temp extends Analyzer {
            @SuppressWarnings("deprecation")
            @Override
            public TokenStream tokenStream(String field, Reader reader)
            {
                Version matchVersion = Version.LUCENE_CURRENT;
                TokenStream result = new LuceneExtendedWhitespaceTokenizer(reader);
                result = new EnglishPossessiveFilter(matchVersion, result);
                result = new LowerCaseFilter(matchVersion, result);
                result = new CommonWordMarkerFilter(result, lexicalDataFactory.getLexicalData(LanguageCode.ENGLISH));
                result = new PorterStemFilter(result);
                return result;
            }
        }
        return new Temp();
    }

    /**
     * Stop POS set for Japanese.
     */
    private Set<String> stopPosSet;

    private Analyzer defaultJapaneseAnalyzer(final ILexicalDataFactory lexicalDataFactory)
    {
        if (stopPosSet == null || reloadResources)
        {
            stopPosSet = DefaultLexicalDataFactory.load(resourceLookup, "stoptags.ja");
            reloadResources = false;
        }

        final class Temp extends Analyzer {
            @SuppressWarnings("deprecation")
            @Override
            public TokenStream tokenStream(String field, Reader reader)
            {
                TokenStream result = new JapaneseTokenizer(
                    reader, null, false, JapaneseTokenizer.DEFAULT_MODE);
                result = new JapaneseBaseFormFilter(result);
                result = new CJKWidthFilter(result);
                result = new CommonWordMarkerFilter(result, lexicalDataFactory.getLexicalData(LanguageCode.JAPANESE));
                result = new JapanesePosCommonWordMarkerFilter(result, stopPosSet);
                result = new JapaneseKatakanaStemFilter(result);
                result = new JapaneseTokenTypeConverter(result);
                result = new LowerCaseFilter(Version.LUCENE_CURRENT, result);
                return result;
            }            
        }
        return new Temp();
    }        
}
