#!/bin/bash

revision=4.1-5
java -jar target/swing-application-owl-${revision}-uber-small.jar provider=treebolic.provider.owl.owlapi.Provider $@

