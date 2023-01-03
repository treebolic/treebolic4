#!/bin/bash

revision=4.0-0
java -jar target/swing-application-owl-${revision}-uber-small.jar provider=treebolic.provider.owl.owlapi.Provider $@

