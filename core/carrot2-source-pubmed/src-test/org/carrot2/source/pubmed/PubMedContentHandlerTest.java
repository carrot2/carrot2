
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

package org.carrot2.source.pubmed;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.carrot2.core.Document;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.io.Resources;

public class PubMedContentHandlerTest extends CarrotTestCase
{
    @Test
    public void testContentHandler() throws Exception
    {
        final XMLReader reader = PubMedDocumentSource.newXmlReader();
        PubMedContentHandler searchHandler = new PubMedContentHandler();
        reader.setContentHandler(searchHandler);
        
        byte [] bytes = Resources.toByteArray(Resources.getResource(PubMedContentHandlerTest.class, "abstracts.xml"));
        reader.parse(new InputSource(new ByteArrayInputStream(bytes)));

        SearchEngineResponse response = searchHandler.getResponse();
        List<String> pmids = Lists.newArrayList();
        for (Document doc : response.results)
        {
            if ("24009777".equals(doc.getStringId())) {
                assertThat(doc.getTitle()).startsWith("Role of choline deficiency in the Fatty liver");
                assertThat(doc.getSummary()).startsWith("Though widely employed for clinical intervention in obesity, metabolic syndrome,");
            }
            if ("24014826".equals(doc.getStringId())) {
                assertThat(doc.getTitle()).startsWith("Myocardial titin hypophosphorylation importantly contributes to");
                assertThat(doc.getSummary())
                    .startsWith("Obesity and diabetes mellitus are important metabolic")
                    .doesNotContain("Four groups of rats (Wistar-Kyoto, n=11;")
                    .doesNotContain("Obese ZSF1 rats developed heart failure with preserved");
            }
            pmids.add(doc.getStringId());
        }

        assertThat(pmids)
            .describedAs("pmids")
            .containsOnly(PubMedIdSearchHandlerTest.PMID_SET.toArray());
        assertThat(response.results).hasSize(150);
    }
}
