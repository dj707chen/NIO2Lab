#!/usr/bin/env bash

mvn compile
mvn exec:java -Dexec.mainClass="async.ServerWithCompletionHandler"

