package org.carrot2.filter.normalizer;


/**
 * Extends {@link CaseNormalizerLocalFilterComponent} using
 * {@link SmartCaseNormalizer}.
 * 
 * @author Dawid Weiss
 */
public final class SmartCaseNormalizerLocalFilterComponent 
    extends CaseNormalizerLocalFilterComponent
{
    public SmartCaseNormalizerLocalFilterComponent() {
        super(new SmartCaseNormalizer());
    }
}
