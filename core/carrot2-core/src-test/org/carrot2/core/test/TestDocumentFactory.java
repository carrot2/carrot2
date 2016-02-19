
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

package org.carrot2.core.test;

import java.util.*;

import org.carrot2.core.Document;

import com.carrotsearch.randomizedtesting.RandomizedContext;

/**
 *
 */
public class TestDocumentFactory
{
    protected static final Map<String, IDataGenerator<?>> DEFAULT_GENERATORS;
    static
    {
        DEFAULT_GENERATORS = new HashMap<String, IDataGenerator<?>>();
        DEFAULT_GENERATORS.put(Document.TITLE, new SentenceGenerator(3, true));
        DEFAULT_GENERATORS.put(Document.SUMMARY, new SentenceGenerator(10));
        DEFAULT_GENERATORS.put(Document.CONTENT_URL, new UrlGenerator(3));
    }

    protected static final Set<String> DEFAULT_FIELDS;
    static
    {
        DEFAULT_FIELDS = new HashSet<String>();
        DEFAULT_FIELDS.addAll(Arrays.asList(Document.TITLE, Document.SUMMARY,
            Document.CONTENT_URL));
    }

    public static final TestDocumentFactory DEFAULT = new TestDocumentFactory(DEFAULT_GENERATORS,
        DEFAULT_FIELDS);

    private final Map<String, IDataGenerator<?>> generators;
    private final Set<String> fields;

    public TestDocumentFactory(Map<String, IDataGenerator<?>> generators, Set<String> fields)
    {
        this.generators = generators;
        this.fields = fields;
    }

    public List<Document> generate(int number)
    {
        return generate(number, fields);
    }

    public List<Document> generate(int number, Set<String> fieldsToGenerate)
    {
        return generate(number, fieldsToGenerate, Collections
            .<String, IDataGenerator<?>> emptyMap());
    }

    public List<Document> generate(int number, Set<String> fieldsToGenerate,
        Map<String, IDataGenerator<?>> customGenerators)
    {
        final List<Document> result = new ArrayList<Document>(number);

        for (int i = 0; i < number; i++)
        {
            final Document document = new Document();
            for (final String field : fieldsToGenerate)
            {
                final IDataGenerator<?> generator = resolveGenerator(customGenerators, field);
                document.setField(field, generator.generate(i));
            }

            result.add(document);
        }

        return result;
    }

    private IDataGenerator<?> resolveGenerator(
        Map<String, IDataGenerator<?>> customGenerators, String field)
    {
        IDataGenerator<?> generator = customGenerators.get(field);

        if (generator == null)
        {
            generator = generators.get(field);
        }

        if (generator == null)
        {
            throw new RuntimeException("No generator for field: " + field);
        }
        return generator;
    }

    public static interface IDataGenerator<T>
    {
        public T generate(int sequentialNumber);
    }

    private static class SentenceGenerator implements IDataGenerator<String>
    {
        private static final String [] WORDS = new String []
        {
            "test", "data", "apple", "London", "PC", "disk", "eclipse", "bank", "pilot",
            "CD"
        };

        private final int words;
        private final boolean prependSequentialNumber;

        public SentenceGenerator(int words)
        {
            this(words, false);
        }

        public SentenceGenerator(int words, boolean prependSequentialNumber)
        {
            this.words = words;
            this.prependSequentialNumber = prependSequentialNumber;
        }

        public String generate(int sequentialNumber)
        {
            final Random rnd = RandomizedContext.current().getRandom();
            final StringBuilder builder = new StringBuilder();

            if (prependSequentialNumber)
            {
                builder.append("[" + sequentialNumber + "] ");
            }

            for (int i = 0; i < words - 1; i++)
            {
                builder.append(WORDS[rnd.nextInt(WORDS.length)]);
                builder.append(" ");
            }
            builder.append(WORDS[rnd.nextInt(WORDS.length)]);

            return builder.toString();
        }
    }

    private static class UrlGenerator implements IDataGenerator<String>
    {
        private static final String [] ELEMENTS = new String []
        {
            "www", "mail", "carrot2", "test", "alpha", "beta"
        };

        private static final String [] DOMAINS = new String []
        {
            "pl", "co.uk", "com", "org", "net"
        };

        private final int length;

        public UrlGenerator(int length)
        {
            this.length = length;
        }

        public String generate(int sequentialNumber)
        {
            final Random rnd = RandomizedContext.current().getRandom();
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < length - 1; i++)
            {
                builder.append(ELEMENTS[rnd.nextInt(ELEMENTS.length)]);
                builder.append(".");
            }
            builder.append(DOMAINS[rnd.nextInt(DOMAINS.length)]);

            return builder.toString();
        }
    }

}
