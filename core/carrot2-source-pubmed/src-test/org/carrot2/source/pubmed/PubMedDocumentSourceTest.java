package org.carrot2.source.pubmed;

import org.carrot2.core.test.QueryableDocumentSourceTestBase;
import org.junit.runner.RunWith;
import org.junitext.runners.AnnotationRunner;

/**
 * Test cases for {@link PubMedDocumentSource}.
 */
@RunWith(AnnotationRunner.class)
public class PubMedDocumentSourceTest extends
    QueryableDocumentSourceTestBase<PubMedDocumentSource>
{
    @Override
    public Class<PubMedDocumentSource> getComponentClass()
    {
        return PubMedDocumentSource.class;
    }

    @Override
    protected boolean canReturnEscapedHtml()
    {
        return false;
    }

    @Override
    protected String getLargeQueryText()
    {
        return "lungs";
    }

    @Override
    protected String getSmallQueryText()
    {
        return "heart";
    }

    @Override
    protected boolean hasTotalResultsEstimate()
    {
        return false;
    }

    @Override
    protected boolean hasUtfResults()
    {
        return false;
    }

    @Override
    protected String getNoResultsQueryText()
    {
        return "chrzęszczyrzeboszyce";
    }
}
