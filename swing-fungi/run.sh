#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=4.1-3
java -jar "${here}/treebolic-fungi-${version}.jar"
