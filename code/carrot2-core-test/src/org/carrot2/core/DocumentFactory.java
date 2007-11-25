/**
 * 
 */
package org.carrot2.core;

import java.util.*;

/**
 *
 */
public final class DocumentFactory
{
    private static final Map<String, DataGenerator<?>> GENERATORS;
    static
    {
        GENERATORS = new HashMap<String, DataGenerator<?>>();
        GENERATORS.put(Document.TITLE, new SentenceGenerator(5));
        GENERATORS.put(Document.SUMMARY, new SentenceGenerator(20));
        GENERATORS.put(Document.CONTENT_URL, new UrlGenerator(3));
    }

    private static final Set<String> DEFAULT_FIELDS;
    static
    {
        DEFAULT_FIELDS = new HashSet<String>();
        DEFAULT_FIELDS.addAll(Arrays.asList(Document.TITLE, Document.SUMMARY,
            Document.CONTENT_URL));
    }

    public static Collection<Document> generate(int number)
    {
        return generate(number, DEFAULT_FIELDS);
    }

    public static Collection<Document> generate(int number, Set<String> fieldsToGenerate)
    {
        return generate(number, fieldsToGenerate, Collections
            .<String, DataGenerator<?>> emptyMap());
    }

    public static Collection<Document> generate(int number, Set<String> fieldsToGenerate,
        Map<String, DataGenerator<?>> customGenerators)
    {
        Collection<Document> result = new ArrayList<Document>(number);

        for (int i = 0; i < number; i++)
        {
            Document document = new Document(i);
            for (String field : fieldsToGenerate)
            {
                DataGenerator<?> generator = resolveGenerator(customGenerators, field);
                document.addField(field, generator.generate());
            }

            result.add(document);
        }

        return result;
    }

    private static DataGenerator<?> resolveGenerator(
        Map<String, DataGenerator<?>> customGenerators, String field)
    {
        DataGenerator<?> generator = customGenerators.get(field);

        if (generator == null)
        {
            generator = GENERATORS.get(field);
        }

        if (generator == null)
        {
            throw new RuntimeException("No generator for field: " + field);
        }
        return generator;
    }

    public static interface DataGenerator<T>
    {
        public T generate();
    }

    public static class SentenceGenerator implements DataGenerator<String>
    {
        private static final String [] WORDS = new String []
        {
            "test", "data", "apple", "London", "PC", "disk", "eclipse", "bank", "pilot",
            "CD"
        };

        private int words;
        private final Random random = new Random(0);

        public SentenceGenerator(int words)
        {
            this.words = words;
        }

        @Override
        public String generate()
        {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < words - 1; i++)
            {
                builder.append(WORDS[random.nextInt(WORDS.length)]);
                builder.append(" ");
            }
            builder.append(WORDS[random.nextInt(WORDS.length)]);

            return builder.toString();
        }
    }

    private static class UrlGenerator implements DataGenerator<String>
    {
        private static final String [] ELEMENTS = new String []
        {
            "www", "mail", "carrot2", "test", "alpha", "beta"
        };

        private static final String [] DOMAINS = new String []
        {
            "pl", "co.uk", "com", "org", "net"
        };

        private int length;
        private final Random random = new Random(0);

        public UrlGenerator(int length)
        {
            this.length = length;
        }

        @Override
        public String generate()
        {
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < length - 1; i++)
            {
                builder.append(ELEMENTS[random.nextInt(ELEMENTS.length)]);
                builder.append(".");
            }
            builder.append(DOMAINS[random.nextInt(DOMAINS.length)]);

            return builder.toString();
        }
    }

}
