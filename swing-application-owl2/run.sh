#!/bin/bash

revision=4.1-7
java -jar target/swing-application-owl-${revision}-uber-small.jar provider=treebolic.provider.owl.owlapi.Provider2 $@

