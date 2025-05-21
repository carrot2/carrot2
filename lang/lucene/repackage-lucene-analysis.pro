-dontskipnonpubliclibraryclasses

-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Synthetic
-keepattributes Signature
-keepattributes MethodParameters
-keepattributes Exceptions
-keepattributes LineNumberTable
-keepattributes SourceFile

-dontoptimize

-keep,allowobfuscation class
  org.apache.lucene.analysis.cz.CzechStemmer,
  org.apache.lucene.analysis.ar.ArabicNormalizer,
  org.apache.lucene.analysis.ar.ArabicStemmer,
  org.apache.lucene.analysis.br.BrazilianStemmer,
  org.apache.lucene.analysis.bg.BulgarianStemmer,
  org.apache.lucene.analysis.gl.GalicianStemmer,
  org.apache.lucene.analysis.el.GreekStemmer,
  org.apache.lucene.analysis.hi.HindiNormalizer,
  org.apache.lucene.analysis.hi.HindiStemmer,
  org.apache.lucene.analysis.in.IndicNormalizer,
  org.apache.lucene.analysis.id.IndonesianStemmer,
  org.apache.lucene.analysis.lv.LatvianStemmer,
  org.apache.lucene.analysis.pt.RSLPStemmerBase
{
    <methods>; <fields>;
}

-adaptresourcefilenames **/*.rslp

# -printmapping build/remap-lucene.map

-renamepackage "org\.apache\.lucene\.analysis\.(ar|br|bg|cz|gl|el|hi|in|id|lv|pt|util)" => "org.carrot2.lucene.analysis"
