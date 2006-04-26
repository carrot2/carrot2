#!/bin/bash
PATH=$PATH:tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux
java $JAVA_OPTS -Djava.library.path=tmp/dist/deps-carrot2-demo-browser-jar/jdic_linux -Djava.ext.dirs=tmp/dist/.:tmp/dist/deps-carrot2-demo-browser-jar carrot2.demo.DemoSwing
