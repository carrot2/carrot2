package org.carrot2.workbench.editors.temp;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;

@Bindable
public class TempClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{

    public TempClusteringAlgorithm()
    {
    }

    /**
     * If set to true, only one cluster with all documents is created.
     * 
     * @label One cluster only
     */
    @Attribute(key = "oneCluster")
    @Input
    @Processing
    private boolean oneCluster;

    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    Collection<Document> documents = Collections.<Document> emptyList();

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    Collection<Cluster> clusters = null;

    @Override
    public void process() throws ProcessingException
    {
        clusters = new ArrayList<Cluster>();
        if (!oneCluster)
        {
            for (Document doc : documents)
            {
                Cluster c = new Cluster();
                c.addPhrases(doc.getField(Document.TITLE).toString());
                c.addDocuments(doc);
                clusters.add(c);
            }
        }
        else
        {
            Cluster c = new Cluster();
            c.addPhrases("All");
            c.addDocuments(documents);
            clusters.add(c);
        }
    }
}
