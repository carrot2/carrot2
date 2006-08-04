package org.carrot2.filter.normalizer;


/**
 * Extends {@link CaseNormalizerLocalFilterComponent} using
 * {@link SimpleCaseNormalizer}.
 * 
 * @author Dawid Weiss
 */
public final class SimpleCaseNormalizerLocalFilterComponent 
    extends CaseNormalizerLocalFilterComponent
{
    public SimpleCaseNormalizerLocalFilterComponent() {
        super(new SimpleCaseNormalizer());
    }
}
