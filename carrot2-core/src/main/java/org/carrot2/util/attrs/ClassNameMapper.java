package org.carrot2.util.attrs;

public interface ClassNameMapper {
  Object fromName(String className);
  String toName(Object value);
}
