#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=@{appversion}
java -jar "${here}/swing-wordnet2-${version}-uber.jar"
