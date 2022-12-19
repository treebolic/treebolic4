#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=3.9.0
java -jar "${here}/treebolic-fungi-${version}.jar"
