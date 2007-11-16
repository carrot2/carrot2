package org.carrot2.sandbox;

import org.carrot2.core.ClusteringAlgorithm;
import org.carrot2.core.parameters.AnyClassTypeWithDefaultValue;
import org.carrot2.core.parameters.Binding;
import org.carrot2.core.parameters.BindingPolicy;
import org.carrot2.core.parameters.ParameterBuilder;
import org.carrot2.core.parameters.ParameterGroup;
import org.carrot2.core.type.BoundedIntegerTypeWithDefaultValue;
import org.carrot2.core.type.Type;

public class ExampleClusteringAlgorithm implements ClusteringAlgorithm
{
    @Binding(BindingPolicy.INSTANTIATION)
    private Tokenizer tokenizer;
    private static Type<?> TOKENIZER = new AnyClassTypeWithDefaultValue(
        Tokenizer.class, ExampleTokenizer.class);

    @Binding(BindingPolicy.RUNTIME)
    private int threshold;
    private static Type<?> THRESHOLD = new BoundedIntegerTypeWithDefaultValue(5, 0, 10);

    /*
     * 
     */
    public ParameterGroup getParameters()
    {
        ParameterGroup pg = new ParameterGroup("ExampleAlgorithm");

        pg.add(ParameterBuilder.getParameters(this.getClass(), BindingPolicy.RUNTIME));
        pg.add(tokenizer.getParameters());

        return pg;
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return "an algorithm, tokenizer=" + tokenizer;
    }
}
