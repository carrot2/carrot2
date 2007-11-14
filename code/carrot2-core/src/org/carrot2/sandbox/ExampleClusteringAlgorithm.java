package org.carrot2.sandbox;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.parameters.Parameter;
import org.carrot2.core.parameters.ParameterGroup;
import org.carrot2.core.type.*;

public class ExampleClusteringAlgorithm implements ClusteringAlgorithm
{
    private Tokenizer tokenizer;

    private enum Test
    {
        VAL1, VAL2;
    }

    public ExampleClusteringAlgorithm(Tokenizer tokenizer)
    {
        this.tokenizer = tokenizer;
    }

    public ParameterGroup getParameters()
    {
        ParameterGroup pg = new ParameterGroup("Example");

        Parameter p1 = new Parameter("a", TypeBuilder.build(20, 10, 50));
        Parameter p2 = new Parameter("b", new EnumTypeWithDefaultValue<Test>(Test.class,
            Test.VAL1));

        pg.add(p1, p2);
        pg.add(tokenizer.getParameters());

        return null;
    }

    /**
     * STATIC (instance-creation time) parameters.
     */
    public static ParameterGroup getInstantiationParameters()
    {
        ParameterGroup pg = new ParameterGroup("Example");
        Parameter p1 = new Parameter("tokenizer", new ConfigurableType<Tokenizer>(
            Tokenizer.class));
        pg.add(p1);
        return pg;
    }

    @Override
    public String toString()
    {
        return "an algorithm, tokenizer=" + tokenizer;
    }
}
