package com.carrotsearch.jsondoclet;

import java.util.List;

public class Sample2 {
  public class PubNested {
    public Integer pubIntField;
    public List<Object> pubListField;
  }

  public static class PubNestedStatic {}

  private class PrvNested {}

  private static class PrvNestedStatic {}
}
