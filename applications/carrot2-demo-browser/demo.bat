@echo off
SET PATH=%PATH%;tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows
java -Djava.library.path=tmp/dist/deps-carrot2-demo-browser-jar/jdic_windows -Djava.ext.dirs=tmp/dist/deps-carrot2-demo-browser-jar -cp tmp/dist/carrot2-demo-browser.jar com.dawidweiss.carrot2.browser.Browser
