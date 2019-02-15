package org.carrot2.util.attrs;

public class JvmNameMapper implements ClassNameMapper {
  public static JvmNameMapper INSTANCE = new JvmNameMapper();

  private JvmNameMapper() {}

  public Object fromName(String className) {
    try {
      return Class.forName(className).getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String toName(Object object) {
    return object.getClass().getName();
  }
}
