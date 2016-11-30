
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v5;

import java.util.concurrent.ExecutionException;

import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.tests.UsesExternalServices;

/** */
@UsesExternalServices
public class Bing5NewsDocumentSourceTest extends MultipageDocumentSourceTestBase<Bing5NewsDocumentSource> {
  @Override
  protected boolean hasTotalResultsEstimate() {
    return true;
  }

  @Override
  public Class<Bing5NewsDocumentSource> getComponentClass() {
    return Bing5NewsDocumentSource.class;
  }

  @Override
  protected MultipageSearchEngineMetadata getSearchEngineMetadata() {
    return Bing5NewsDocumentSource.METADATA;
  }
  
  @Override
  public void testInCachingController() throws InterruptedException, ExecutionException {
    super.testInCachingController();
  }
  
  @Override
  protected String getSmallQueryText() {
    return "usa";
  }
  
  @Override
  protected boolean canReturnMoreResultsThanRequested() {
    return true;
  }

  @Override
  protected boolean hasUtfResults() {
    return true;
  }
}
