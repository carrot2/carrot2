
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.microsoft.v7;

import org.carrot2.core.test.MultipageDocumentSourceTestBase;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.util.tests.UsesExternalServices;
import org.junit.*;

/** */
@UsesExternalServices
public class Bing7DocumentSourceTest extends MultipageDocumentSourceTestBase<Bing7DocumentSource> {
  @Before
  public void checkKeyAvailable() {
    Assume.assumeTrue(System.getProperty(Bing7DocumentSource.SYSPROP_BING7_API) != null);
  }
  
  @Override
  protected boolean hasTotalResultsEstimate() {
    return true;
  }

  @Override
  public Class<Bing7DocumentSource> getComponentClass() {
    return Bing7DocumentSource.class;
  }

  @Override
  protected MultipageSearchEngineMetadata getSearchEngineMetadata() {
    return Bing7DocumentSource.METADATA;
  }

  @Override
  protected boolean hasUtfResults() {
    return true;
  }
}
