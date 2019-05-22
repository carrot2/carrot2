package org.carrot2.attrs;

public interface ClassNameMapper {
  Object fromName(String className);

  String toName(Object value);
}
