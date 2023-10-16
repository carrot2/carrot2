/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.text.preprocessing;

/** Formats cluster labels for final rendering. */
public interface LabelFormatter {
  /**
   * @param image images of the words making the label.
   * @param stopWord determines whether the corresponding word of the label is a stop word
   */
  String format(char[][] image, boolean[] stopWord);
}
