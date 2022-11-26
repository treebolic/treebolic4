#!/bin/bash

revision=4.0-SNAPSHOT
java -jar target/swing-application-owl-${revision}-uber-small.jar provider=treebolic.provider.owl.owlapi.Provider $@

