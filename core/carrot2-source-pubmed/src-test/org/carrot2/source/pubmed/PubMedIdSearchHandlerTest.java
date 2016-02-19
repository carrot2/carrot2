
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
import java.util.ArrayList;

import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.io.Resources;

public class PubMedIdSearchHandlerTest extends CarrotTestCase
{
    static ArrayList<String> PMID_SET = Lists.newArrayList(
        "24470789",
        "24469991",
        "24468157",
        "24463729",
        "24462788",
        "24462051",
        "24458711",
        "24454330",
        "24445216",
        "24445045",
        "24445044",
        "24444538",
        "24438079",
        "24429541",
        "24424058",
        "24404629",
        "24397951",
        "24394536",
        "24388434",
        "24387274",
        "24385344",
        "24380833",
        "24379686",
        "24377894",
        "24374005",
        "24370063",
        "24369828",
        "24368111",
        "24367585",
        "24366220",
        "24364133",
        "24362355",
        "24358329",
        "24346072",
        "24345049",
        "24342780",
        "24340336",
        "24338487",
        "24333965",
        "24333561",
        "24333157",
        "24330576",
        "24325088",
        "24312343",
        "24303175",
        "24298777",
        "24290571",
        "24287404",
        "24283215",
        "24281397",
        "24275089",
        "24267039",
        "24266602",
        "24263037",
        "24259558",
        "24247152",
        "24234673",
        "24229349",
        "24225358",
        "24220118",
        "24219891",
        "24202306",
        "24196354",
        "24194732",
        "24176230",
        "24172199",
        "24171041",
        "24170386",
        "24163396",
        "24163066",
        "24148349",
        "24148164",
        "24140661",
        "24139973",
        "24124374",
        "24123166",
        "24117264",
        "24116221",
        "24107491",
        "24106600",
        "24101673",
        "24098813",
        "24098551",
        "24098525",
        "24084690",
        "24081993",
        "24080184",
        "24075193",
        "24072533",
        "24072531",
        "24069865",
        "24066597",
        "24063548",
        "24060958",
        "24050894",
        "24050803",
        "24047636",
        "24044965",
        "24044579",
        "24036126",
        "24030551",
        "24029787",
        "24019901",
        "24015695",
        "24015188",
        "24014826",
        "24014675",
        "24009777",
        "24005471",
        "24000103",
        "23999430",
        "23999279",
        "23994198",
        "23986204",
        "23984293",
        "23981691",
        "23981577",
        "23974119",
        "23973955",
        "23970917",
        "23964081",
        "23954796",
        "23954368",
        "23948693",
        "23945609",
        "23939686",
        "23939398",
        "23936994",
        "23934850",
        "23929677",
        "23928364",
        "23926027",
        "23923985",
        "23922128",
        "23921137",
        "23913707",
        "23911141",
        "23906130",
        "23902937",
        "23902780",
        "23895132",
        "23894285",
        "23887640",
        "23885014",
        "23884889",
        "23884883",
        "23881149",
        "23876511",
        "23875703",
        "23874490");
    

    @Test
    public void testIdHandler() throws Exception
    {
        final XMLReader reader = PubMedDocumentSource.newXmlReader();
        PubMedIdSearchHandler searchHandler = new PubMedIdSearchHandler();
        reader.setContentHandler(searchHandler);
        
        byte [] bytes = Resources.toByteArray(Resources.getResource(PubMedIdSearchHandlerTest.class, "ids.xml"));
        reader.parse(new InputSource(new ByteArrayInputStream(bytes)));

        assertThat(searchHandler.getMatchCount()).isEqualTo(4561L);
        assertThat(searchHandler.getPubMedPrimaryIds()).isEqualTo(PMID_SET);
    }
}
