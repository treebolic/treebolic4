#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

ds="../src/main/resources/treebolic/provider/owl/owlapi/images"
l1="*"
l2="root"
#echo $l1
#echo $l2

#declare -A res
#res=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)

for d in $ds; do

	# 1
	res=32

	mkdir -p ${d}
	for f in ${l1}.svg; do
		img=${f%.*}
		echo "make ${img}.svg -> ${d}/${img}.png @ resolution ${res}"
		inkscape ${img}.svg --export-png=${d}/${img}.png -h ${res} > /dev/null 2> /dev/null
	done

	# 2
	res=48

	mkdir -p ${d}
	for f in ${l2}.svg; do
		img=${f%.*}
		echo "make ${img}.svg -> ${d}/${img}.png @ resolution ${res}"
		inkscape ${img}.svg --export-png=${d}/${img}.png -h ${res} > /dev/null 2> /dev/null
	done

done
