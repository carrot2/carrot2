@echo off

SET PATH=%PATH%;tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows

java %JAVA_OPTS% -Djava.library.path=tmp\dist\deps-carrot2-demo-browser-jar\jdic_windows -jar tmp\dist\deps-carrot2-demo-browser-jar\carrot2-launcher.jar -cpdir tmp\dist\. -cpdir tmp\dist\deps-carrot2-demo-browser-jar org.carrot2.demo.DemoSplash /org/carrot2/demo/carrot2-splash.png 4 org.carrot2.demo.DemoSwing