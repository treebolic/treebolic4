#!/bin/bash

apps="
 	swing-application-xml-dom
 	swing-application-xml-sax
 	swing-application-xml-stax
 	swing-application-owl
 	swing-application-owl2
 	swing-application-owl-jena
 	swing-application-owl-sax
 	swing-files
 	swing-fungi
 	swing-wordnet
 	swing-wordnet2
"

installers="
	swing-fungi-installer
	swing-wordnet2-installer
	swing-studio-installer
"

function dump()
{
  local goal=$1
  shift
  local modules=$@
  echo ${goal}
  echo ${modules}
}

function maven()
{
  local goal=$1
  shift
  local modules=$@
  echo ${goal}
  echo ${modules}
  for p in ${modules}; do
    echo ${p}
    if ! mvn -f ${p}/pom.xml clean ${goal}; then
      exit $?
      fi
  done
}

maven install ${apps}
maven package ${installers}
