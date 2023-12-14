#!/bin/sh
# bbou@ac-toulouse
# dim 10 nov 2002 09:39:38 CET 

version=3.9.0
stamp=`date +%Y%m%d`
/opt/javatools/izpack/bin/compile izpack-install.xml -b ../.. -o treebolic-wordnet-installer-${version}-${stamp}.jar -k standard
