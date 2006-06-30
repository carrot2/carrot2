
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package carrot2.demo.webstart;

import java.util.HashMap;

import carrot2.demo.cache.RawDocumentProducerCacheWrapper;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.impl.RawDocumentDummyLanguageDetection;
import com.dawidweiss.carrot.core.local.impl.RawDocumentEnumerator;
import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.filter.stc.local.STCLocalFilterComponent;
import com.dawidweiss.carrot.input.yahoo.YahooApiInputComponent;
import com.dawidweiss.carrot.util.tokenizer.SnippetTokenizerLocalFilterComponent;
import com.dawidweiss.carrot.util.tokenizer.languages.english.English;
import com.kgolembniak.carrot.filter.haogstc.local.HAOGSTCLocalFilterComponent;
import com.stachoodev.carrot.filter.lingo.local.LingoLocalFilterComponent;
import com.stachoodev.carrot.filter.normalizer.SmartCaseNormalizer;
import com.stachoodev.carrot.filter.normalizer.local.CaseNormalizerLocalFilterComponent;

/**
 * A brute-force workaround for problems with security exceptions when Beanshell
 * attempts to create anonymous classes in a (signed!) WebStart application: we
 * will create components using a public factory-like class.
 * 
 * @author Dawid Weiss
 */
public class WebStartComponentFactory {

    /**
     * <code>filter-tokenizer</code>
     */
    public static LocalComponentFactory createTokenizer() {
        final LocalComponentFactory factory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new SnippetTokenizerLocalFilterComponent();
            }
        };

        return factory;
    }

    /**
     * <code>filter-language-detection-xx</code>
     */
    public static LocalComponentFactory createLanguageDetection(final String langCode) {
        final LocalComponentFactory factory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new RawDocumentDummyLanguageDetection(langCode);
            }
        };

        return factory;
    }
    
    /**
     * <code>filter-case-normalizer</code>
     */
    public static LocalComponentFactory createCaseNormalizer() {
        final LocalComponentFactory factory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new CaseNormalizerLocalFilterComponent(
                    new SmartCaseNormalizer());
            }
        };
        return factory;
    }
    
    /**
     * <code>input-cached-yahooapi</code>
     */
    public static LocalComponentFactory createCachedYahooApi() {
        final LocalComponentFactory factory = new LocalComponentFactory() {
            public LocalComponent getInstance() {
                // Wrap Yahoo API component with the cache
                return new RawDocumentProducerCacheWrapper(
                        new YahooApiInputComponent(), YahooApiInputComponent.class);
            }
        };
        return factory;
    }

    /**
     * <code>filter-stc</code>
     */
    public static LocalComponentFactory createStc() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new STCLocalFilterComponent();
            }
        };
    }
    
    /**
     * <code>filter-rough-kmeans</code>
     */
    public static LocalComponentFactory createRoughKMeans() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new com.chilang.carrot.filter.cluster.local.RoughKMeansLocalFilterComponent();
            }
        };
    }
    
    /**
     * <code>filter-rawdocument-enumerator</code> 
     */
    public static LocalComponentFactory createRawdocumentEnumerator() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new RawDocumentEnumerator();
            }
        };        
    }
    
    /**
     * <code>filter-lingo</code>
     */
    public static LocalComponentFactory createLingo() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                final HashMap params = new HashMap();
                final Language [] languages = new Language [] {
                    new English()
                };
                return new LingoLocalFilterComponent(languages, params);
            }
        };
    }
    
    /**
     * <code>filter-haog-stc</code>
     */
    public static LocalComponentFactory createHaogStc() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new HAOGSTCLocalFilterComponent();
            }
        };
    }
    
    /**
     * <code>filter-fuzzyants</code> 
     */
    public static LocalComponentFactory createFuzzyAnts() {
        return new LocalComponentFactory() {
            public LocalComponent getInstance() {
                return new fuzzyAnts.FuzzyAntsLocalFilterComponent();
            }
        };
    }
}
