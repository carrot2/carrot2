@echo off
SET PATH=%PATH%;tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows
java %JAVA_OPTS% -Djava.library.path=tmp/dist/deps-carrot2-demo-browser-jar/jdic_windows -Djava.ext.dirs=tmp/dist/deps-carrot2-demo-browser-jar -cp tmp/dist/carrot2-demo-browser.jar carrot2.demo.DemoSwing
