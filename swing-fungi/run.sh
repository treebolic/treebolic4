#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=4.1-7
java -jar "${here}/treebolic-fungi-${version}.jar"
