package org.carrot2.workbench.editors.temp;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.attribute.constraint.IntRange;

@Bindable
public class TempClusteringAlgorithm extends ProcessingComponentBase implements
    ClusteringAlgorithm
{

    public static interface AmountInterface
    {
        int getAmount();
    }

    public static class Amount2 implements AmountInterface
    {

        public int getAmount()
        {
            return 2;
        }

    }

    public static class Amount3 implements AmountInterface
    {

        public int getAmount()
        {
            return 3;
        }

    }

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

    /**
     * Some description.
     * 
     * dfgshdhfsjhdsgsr
     * 
     * @label Amount
     */
    @SuppressWarnings("unused")
    @Attribute(key = "someObject")
    @Input
    @Processing
    @ImplementingClasses(classes =
    {
        Amount2.class, Amount3.class
    })
    private AmountInterface someObject = new Amount2();

    @Processing
    @Input
    @Attribute(key = AttributeNames.DOCUMENTS)
    Collection<Document> documents = Collections.<Document> emptyList();

    @SuppressWarnings("unused")
    @Processing
    @Output
    @Attribute(key = AttributeNames.CLUSTERS)
    Collection<Cluster> clusters = null;

    /**
     * Number of docs in the cluster.
     * 
     * @label Number of docs
     */
    @Attribute
    @Processing
    @Input
    @IntRange(min = 1, max = 20)
    int rangeAttribute = 3;

    @Override
    public void process() throws ProcessingException
    {
        clusters = new ArrayList<Cluster>();
        List<Cluster> tempClusters = new ArrayList<Cluster>();
        if (!oneCluster)
        {
            for (Document doc : documents)
            {
                Cluster c = new Cluster();
                c.addPhrases(doc.getField(Document.TITLE).toString());
                c.addDocuments(doc);
                tempClusters.add(c);
            }
            int i = 0;
            while (i + rangeAttribute < tempClusters.size())
            {
                Cluster c = new Cluster();
                c.addPhrases("Cluster " + i);
                for (int j = 0; j < rangeAttribute; j++)
                {
                    c.addSubclusters(tempClusters.get(i + j));
                }
                clusters.add(c);
                i += rangeAttribute;
            }
            {
                Cluster c = new Cluster();
                c.addPhrases("Cluster " + i);
                for (int j = i; j < tempClusters.size(); j++)
                {
                    c.addSubclusters(tempClusters.get(j));
                }
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
