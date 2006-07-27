@echo off
SET PATH=%PATH%;tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows
java %JAVA_OPTS% -Djava.library.path=tmp/dist/deps-carrot2-demo-browser-jar/jdic_windows -Djava.ext.dirs=tmp/dist/.;tmp/dist/deps-carrot2-demo-browser-jar org.carrot2.demo.DemoSwing
