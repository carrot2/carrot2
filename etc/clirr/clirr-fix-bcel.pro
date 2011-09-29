#
# Temporarily fixes clirr by proguard-renaming embedded obsolete libraries. Requires
# dweiss/github/proguard clone with package renamer.
# java -jar proguard.jar @clirr*.pro
#

-injars  clirr-core-0.6-uber.jar(!**/package.html)
-outjars clirr-core-0.6-fixed.jar
-libraryjars <java.home>/lib/rt.jar(java/**)

-dontnote
-dontoptimize
-dontwarn

-renamepackage org.apache=>net.sf.clirr.dependencies

-keep class net.sf.clirr.** {
    <methods>; <fields>;
}
