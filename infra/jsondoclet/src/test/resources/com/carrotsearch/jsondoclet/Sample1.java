package com.carrotsearch.jsondoclet;

import java.util.List;

/**
 * First line type summary. Second sentence of the summary.
 *
 * New paragraph, no HTML tag.
 *
 * <p>New paragraph, with html code.
 */
public class Sample1 {

  /**
   * First line of field summary. Second sentence of field summary.
   *
   * A <i>html tag</i>.
   *
   * An <i>unclosed html tag.
   *
   * A javadoc {@link #pubListField} field link.
   *
   * A javadoc {@link Sample2} type link.
   *
   * @see "https://www.google.com"
   * @see Sample2
   * @see #pubListField
   */
  public Integer pubIntField;
  public List<Object> pubListField;

  private Integer prvIntField;
  private List<Object> prvListField;

  /** First line constructor summary. Second sentence of the summary. */
  public Sample1() {}

  public void publicMethod1() {}

  private void privateMethod1() {}
}
