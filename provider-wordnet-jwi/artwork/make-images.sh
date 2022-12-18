#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"

hierarchy="hypernym hyponym instance.hypernym instance.hyponym holonym meronym member.holonym member.meronym part.holonym part.meronym substance.holonym substance.meronym"
lex="antonym"
verb="cause caused entail entailed verb.group participle"
adj="similar attribute"
adv="adjderived"
deriv="derivation"
misc="also pertainym synonym"
domain="domain domain.member domain.category domain.member.category domain.region domain.member.region domain.term domain.member.term domain.usage domain.member.usage"

pos="pos pos.n pos.v pos.a pos.s pos.r"
utils="focus category sense synonym synset members links item other ${pos}"
utils2="reflexive semantic lexical"

l1="${hierarchy} ${lex} ${verb} ${adj} ${adv} ${deriv} ${misc} ${domain} ${utils}"
l2="${l1} ${utils2}"
#echo $l1
#echo $l2

#declare -A res
#res=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144)

# provider
res=48
d="../src/treebolic/provider/wordnet/jwi/images"
mkdir -p ${d}
for img in ${l1}; do
	echo "make ${img}.svg -> ${d}/${img}.png @ resolution ${res}"
	inkscape ${img}.svg --export-png=${d}/${img}.png -w${res} > /dev/null 2> /dev/null
done
img=item
res=$((res / 2))
echo "make ${img}.svg -> ${d}/${img}.png @ resolution ${res}"
inkscape ${img}.svg --export-png=${d}/${img}.png -w${res} > /dev/null 2> /dev/null
exit

# treebolic browser doc
res=32
d="../../treebolic-browser-wordnet/src/treebolic/wordnet/browser/doc/images"
mkdir -p ${d}
for img in ${l2}; do
	echo "make ${img}.png -> ${d}/${img}.png"
	inkscape ${img}.svg --export-png=${d}/${img}.png -h${res} > /dev/null 2> /dev/null
done

# treebolic browser doc
res=32
d="../../treebolic-browser-wordnet/src/treebolic/wordnet/browser/doc/images"
mkdir -p ${d}
for img in ${l2}; do
	echo "make ${img}.png -> ${d}/${img}.png"
	inkscape ${img}.svg --export-png=${d}/${img}.png -h${res} > /dev/null 2> /dev/null
done

