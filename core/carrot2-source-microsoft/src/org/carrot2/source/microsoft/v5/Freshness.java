package org.carrot2.source.microsoft.v5;

public enum Freshness {
  DAY("Day"),
  WEEK("Week"),
  MONTH("Month");
  
  public final String argName;

  private Freshness(String argName) {
    this.argName = argName;
  }
}
