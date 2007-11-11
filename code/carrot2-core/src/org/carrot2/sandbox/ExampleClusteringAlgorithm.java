package org.carrot2.sandbox;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.parameters.ComponentTypeMetadata;
import org.carrot2.core.parameters.IntegerTypeMetadata;
import org.carrot2.core.parameters.Parameter;
import org.carrot2.core.parameters.ParameterGroup;

public class ExampleClusteringAlgorithm implements ClusteringAlgorithm
{
    private Tokenizer tokenizer;

    public ExampleClusteringAlgorithm(Tokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
    }

    public ParameterGroup getParameters()
    {
        ParameterGroup pg = new ParameterGroup("Example");

        Parameter p1 = new Parameter("a", new IntegerTypeMetadata(0, 100));
        Parameter p2 = new Parameter("b", new IntegerTypeMetadata(0, 100));

        pg.addAll(p1, p2);
        pg.add(tokenizer.getParameters());

        return null;
    }
    
    /**
     * STATIC (instance-creation time) parameters.
     */
    public static ParameterGroup getInstantiationParameters()
    {
        ParameterGroup pg = new ParameterGroup("Example");
        Parameter p1 = new Parameter("tokenizer",
            new ComponentTypeMetadata(Tokenizer.class));
        pg.addAll(p1);
        return pg;
    }

    @Override
    public String toString()
    {
        return "an algorithm, tokenizer=" + tokenizer;
    }
}
