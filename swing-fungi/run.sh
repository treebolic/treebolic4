#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=4.1-10
java -jar "${here}/swing-fungi-${version}-uber.jar"
