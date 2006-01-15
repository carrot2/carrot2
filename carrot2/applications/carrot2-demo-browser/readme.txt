
This is a demo of Carrot2 local components binding.

Build from sources
------------------

ant -q build


Run the binary
--------------

Use "demo" scripts provided in the main folder.


Forcing Java HTML browser
--------------

By default the demo uses JDIC (native) browser component under Windows.
To use a pure Java browser, set the following JVM property:

use.java.browser=true

A convenient way to do it is to define JAVA_OPTS property:

JAVA_OPTS=-Duse.java.browser=true