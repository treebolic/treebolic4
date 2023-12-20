#!/bin/bash

#
# Copyright (c) 2023. Bernard Bou
#

here=`readlink -f $0`
here=`dirname "${here}"`
version=@{appversion}
java -jar "${here}/swing-fungi-${version}-uber.jar"
