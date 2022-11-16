#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

d="../src/treebolic/glue/component/images"
mkdir -p ${d}

res=24
for svg in *.svg; do
	png="${svg%.svg}.png"
	echo "${svg}.svg -> ${d}/${png}.png @ resolution ${res}"
	inkscape ${svg} --export-png=${d}/${png} -h${res} > /dev/null 2> /dev/null
done

res=16
for svg in search*.svg; do
	png="${svg%.svg}.png"
	echo "${svg}.svg -> ${d}/${png}.png @ resolution ${res}"
	inkscape ${svg} --export-png=${d}/${png} -h${res} > /dev/null 2> /dev/null
done

res=12
for svg in status_plus.svg status_minus.svg; do
	png="${svg%.svg}.png"
	echo "${svg}.svg -> ${d}/${png}.png @ resolution ${res}"
	inkscape ${svg} --export-png=${d}/${png} -h${res} > /dev/null 2> /dev/null
done

