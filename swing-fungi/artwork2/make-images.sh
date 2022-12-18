#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
cd thisdir

r=24
d=../src/treebolic/fungi/browser/images

mkdir -p ${d}
for svg in *.svg; do
	png="${svg%.svg}.png"
	echo "${svg}.svg -> ${d}/${png}.png @ resolution ${r}"
	inkscape ${svg} --export-png=${d}/${png} -w ${r} -h${r} > /dev/null 2> /dev/null
done

