#!/bin/bash
# bbou@ac-toulouse
# 31.05.2008  

debug=-DTRACE=TRUE
dir=.

pushd $dir > /dev/null
installer=`find . -name "treebolic-install*.jar" | sort | tail -n 1`
java $debug -jar $installer
popd $dir > /dev/null

