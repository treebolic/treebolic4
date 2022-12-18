#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirfrom="/opt/devel/android-treebolic-one/TreebolicFungi/datafactory"
dirto="../"
zipfile=data.zip
images="*.png *.jpg"
images="*.png"

# update
cp $dirfrom/fungi.db fungi.db

# zip
rm $dirto/$zipfile
zip $dirto/$zipfile *.db *.properties ${images}

