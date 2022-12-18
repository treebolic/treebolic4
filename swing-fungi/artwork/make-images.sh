#!/bin/bash

#
# Copyright (c) 2022. Bernard Bou
#

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"


d="../src/treebolic/fungi/browser/splash/images"
mkdir -p ${d}

img=treebolic-logo
r=48
inkscape ${img}.svg --export-png=${d}/${img}.png -h${r} > /dev/null 2> /dev/null

img=treebolic-fungi
r=256
inkscape ${img}.svg --export-png=${d}/${img}.png -h${r} > /dev/null 2> /dev/null

img=google-play
r=128
inkscape ${img}.svg --export-png=${d}/${img}.png -w${r} > /dev/null 2> /dev/null

img=android
r=48
inkscape ${img}.svg --export-png=${d}/${img}.png -h${r} > /dev/null 2> /dev/null

d="../../dist/install-treebolic-fungi"
mkdir -p ${d}

img=treebolic-fungi
r=128
inkscape ${img}.svg --export-png=${d}/${img}.png -h${r} > /dev/null 2> /dev/null

mkdir -p ${d}
img=treebolic-fungi
r=128
inkscape ${img}.svg --export-png=${d}/${img}.ico.png -h${r}
convert ${d}/${img}.ico.png ${d}/${img}.ico
rm ${d}/${img}.ico.png
