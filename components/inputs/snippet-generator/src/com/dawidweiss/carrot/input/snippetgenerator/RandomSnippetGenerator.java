

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.dawidweiss.carrot.input.snippetgenerator;


import edu.cornell.lassp.houle.RngPack.*;
import java.io.*;
import javax.servlet.http.*;


/**
 * This class is an input type component for Carrot2 and it generates a random set of search
 * results snippets (but for each unique query string the generated set of snippets is exactly the
 * same). "Terms" in the output are picked randomly from the set of characters between 'a' and
 * 'z'. The length of each term varies between 1 and 10 (also picked randomly).
 */
public class RandomSnippetGenerator
    extends com.dawidweiss.carrot.input.InputRequestProcessor
{
    protected static final int TITLE_MIN_LENGTH = 0;
    protected static final int TITLE_MAX_LENGTH = 10;
    protected static final int SNIPPET_MIN_LENGTH = 0;
    protected static final int SNIPPET_MAX_LENGTH = 30;
    protected static final int WORD_MIN_LENGTH = 1;
    protected static final int WORD_MAX_LENGTH = 10;

    /**
     * Processes the query and writes the result to the output stream.
     *
     * @param query User query
     * @param output The stream, where output XML will be saved to.
     * @param requestedResultsNumber The requested number of results.
     * @param request HttpRequest which caused this processing. not used by this component.
     */
    public void processQuery(
        String query, int requestedResultsNumber, Writer output, HttpServletRequest request
    )
    {
        long seed = query.hashCode();
        RandomSeedable rnd = new Ranecu(seed);

        // output the stream beginning
        try
        {
            output.write("<searchresult>");

            output.write(
                "<query requested-results=\"" + requestedResultsNumber + "\"><![CDATA[" + query
                + "]]></query>\n"
            );

            // emit the output snippets
            StringBuffer buffer = new StringBuffer(300);

            for (int i = 0; i < requestedResultsNumber; i++)
            {
                buffer.setLength(0);

                // emit <document id="ID">\n
                buffer.append("<document id=\"");
                buffer.append(i + 1);
                buffer.append("\">\n");

                // emit the title
                buffer.append("\t<title>");
                emitTitle(rnd, buffer);
                buffer.append("</title>\n\t");

                // emit the URL
                buffer.append("\t<url>");
                emitURL(rnd, buffer);
                buffer.append("</url>\n");

                // emit a snippet
                buffer.append("\t<snippet>");
                emitSnippet(rnd, buffer);
                buffer.append("</snippet>\n");

                // emit the end of a document
                buffer.append("</document>\n\n");

                output.write(buffer.toString());
            }

            output.write("</searchresult>");
        }
        catch (IOException e)
        {
            // should we do something about it? just log an error?
        }
    }


    // ------------------------------------------------------- protected section

    /**
     * Outputs a random title of a document. The title is between 0 and 10 words long.
     */
    protected void emitTitle(RandomSeedable rnd, StringBuffer buffer)
    {
        for (int i = 0; i < rnd.choose(TITLE_MIN_LENGTH, TITLE_MAX_LENGTH); i++)
        {
            if (i > 0)
            {
                buffer.append(' ');
            }

            emitWord(rnd, buffer);
        }
    }


    /**
     * Emits a "random" url.
     */
    protected void emitURL(RandomSeedable rnd, StringBuffer buffer)
    {
        buffer.append("http://www.");

        for (int i = 0; i < rnd.choose(1, 3); i++)
        {
            emitWord(rnd, buffer);
            buffer.append(".");
        }

        emitWord(rnd, buffer);
    }


    /**
     * Emits a snippet. Each snippet consists of several terms, occasionaly separated using a dot
     * character.
     */
    protected void emitSnippet(RandomSeedable rnd, StringBuffer buffer)
    {
        for (int i = 0; i < rnd.choose(SNIPPET_MIN_LENGTH, SNIPPET_MAX_LENGTH); i++)
        {
            if (i > 0)
            {
                int sentenceMark = rnd.choose(0, 10);

                if (sentenceMark == 10)
                {
                    buffer.append('.');
                }
                else
                {
                    buffer.append(' ');
                }
            }

            emitWord(rnd, buffer);
        }
    }


    /**
     * Emits a word.
     */
    protected void emitWord(RandomSeedable rnd, StringBuffer buffer)
    {
        for (int i = 0; i < rnd.choose(WORD_MIN_LENGTH, WORD_MAX_LENGTH); i++)
        {
            buffer.append((char) rnd.choose('a', 'z'));
        }
    }
}
