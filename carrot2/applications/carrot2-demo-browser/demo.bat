@echo off

SET PATH=%PATH%;tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows

java %JAVA_OPTS% -Djava.library.path=tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows -jar tmp\dist\deps-carrot2-demo-browser-jar\carrot2-launcher.jar -cpdir tmp\dist\. -cpdir tmp\dist\deps-carrot2-demo-browser-jar org.carrot2.demo.DemoSplash /res/browser-splash.png 5 org.carrot2.demo.DemoSwing