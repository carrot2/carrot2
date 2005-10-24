#!/bin/bash
PATH=$PATH:tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux
java -Djava.library.path=tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux -Djava.ext.dirs=tmp/dist/deps-carrot2-demo-browser-jar -cp tmp/dist/carrot2-demo-browser.jar carrot2.demo.DemoSwing
