@SuppressWarnings("module")
module org.carrot2.core {
  requires com.carrotsearch.hppc;

  exports org.carrot2.attrs;
  exports org.carrot2.clustering.kmeans;
  exports org.carrot2.clustering.lingo;
  exports org.carrot2.clustering.stc;
  exports org.carrot2.clustering;
  exports org.carrot2.internal.nanojson;
  exports org.carrot2.language.snowball;
  exports org.carrot2.language;
  exports org.carrot2.text.preprocessing.filter;
  exports org.carrot2.text.preprocessing;
  exports org.carrot2.text.suffixtree;
  exports org.carrot2.text.vsm;
  exports org.carrot2.util;
}
