package org.carrot2.clustering.lingo.japanese;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.test.ClusteringAlgorithmTestBase;
import org.carrot2.text.clustering.MultilingualClusteringDescriptor;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Development tests only.")
public class JapaneseLingoClusteringTest extends
    ClusteringAlgorithmTestBase<LingoClusteringAlgorithm>
{
    @Override
    public Class<LingoClusteringAlgorithm> getComponentClass()
    {
        return LingoClusteringAlgorithm.class;
    }

    @Test
    public void clusterAndTranslate() throws Exception
    {
        MultilingualClusteringDescriptor.attributeBuilder(initAttributes)
            .defaultLanguage(LanguageCode.JAPANESE);
        
        String [] docSets = {
            "tokyo.ja.xml",
            "sushi.ja.xml",
            "datamining.ja.xml"
        };
        final Controller controller = getSimpleController(initAttributes);
        try {
            for (String dataSet : docSets) {
                ProcessingResult in = 
                    ProcessingResult.deserialize(getClass().getResourceAsStream(dataSet));
    
                ProcessingResult out = controller.process(
                    in.getDocuments(), 
                    (String) in.getAttribute(AttributeNames.QUERY), 
                    getComponentClass());

                System.out.println("--- " + dataSet);
                for (Cluster c : out.getClusters()) {
                    System.out.println(c.getLabel());
                }
            }
        } finally {
            controller.dispose();
        }
    }
}
