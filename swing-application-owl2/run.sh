#!/bin/bash

revision=4.1-10
java -jar target/swing-application-owl-${revision}-uber-small.jar provider=treebolic.provider.owl.owlapi.Provider2 $@

