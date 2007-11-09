package org.carrot2.core;

public class ExampleClusteringAlgorithm implements ClusteringAlgorithm
{
    private Tokenizer tokenizer;

    public ParameterGroup getParameters()
    {
        ParameterGroup pg = new ParameterGroup("Example");

        Parameter p1 = new Parameter("a", new IntegerTypeMetadata(0, 100));
        Parameter p2 = new Parameter("b", new IntegerTypeMetadata(0, 100));

        pg.addAll(p1, p2);
        pg.add(tokenizer.getParameters());

        return null;
    }
}
