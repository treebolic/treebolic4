#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=4.2-1
java -jar "${here}/swing-wordnet-${version}-uber.jar"
