
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Test cases for {@link DocumentBuilder}.
 */
public class DocumentBuilderTest extends CarrotTestCase
{
    @Test
    public void testConstruction()
    {
        final Document doc = new DocumentBuilder()
            .id("custom-id")
            .title("title")
            .summary("summary")
            .contentURL("fake://content-uri")
            .clickURL("fake://click-uri")
            .thumbURL("fake://thumb-uri")
            .sources("source-A", "source-B")
            .language(LanguageCode.POLISH)
            .attr("custom-attr", "foo")
            .attr("custom-attr-integer", 23)
            .build();

        assertThat(doc.getStringId()).isEqualTo("custom-id");
        assertThat(doc.getTitle()).isEqualTo("title");
        assertThat(doc.getSummary()).isEqualTo("summary");
        assertThat(doc.getContentURL()).isEqualTo("fake://content-uri");
        assertThat(doc.getClickURL()).isEqualTo("fake://click-uri");
        assertThat(doc.getThumbURL()).isEqualTo("fake://thumb-uri");
        assertThat(doc.getSources()).containsOnly("source-A", "source-B");
        assertThat(doc.getLanguage()).isEqualTo(LanguageCode.POLISH);
        assertThat(doc.getAttribute("custom-attr")).isEqualTo("foo");
        assertThat(doc.getAttribute("custom-attr-integer")).isEqualTo(23);
    }
}
