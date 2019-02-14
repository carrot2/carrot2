
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

package org.carrot2.text.vsm;

import org.carrot2.util.attrs.AttrComposite;

/**
 * Calculates term-document matrix element values based on Term Frequency.
 */
public class LogTfIdfTermWeighting extends AttrComposite implements ITermWeighting {
  public double calculateTermWeight(int termFrequency, int documentFrequency,
                                    int documentCount) {
    if (documentFrequency > 0) {
      return termFrequency * Math.log(documentCount / (double) documentFrequency);
    } else {
      return 0;
    }
  }
}
