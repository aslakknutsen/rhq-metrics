#!/bin/sh

java -Djava.net.preferIPv4Stack=true \
   -cp ${HOME}/.m2/repository/io/netty/netty-all/4.0.19.Final/netty-all-4.0.19.Final.jar\
:ptrans.properties\
:target/ptrans-0.1.0-SNAPSHOT.jar\
:${HOME}/.m2/repository/org/slf4j/slf4j-log4j12/1.7.7/slf4j-log4j12-1.7.7.jar\
:${HOME}/.m2/repository/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar\
:${HOME}/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar\
:${HOME}/.m2/repository/org/acplt/oncrpc/1.0.7/oncrpc-1.0.7.jar\
 org.rhq.metrics.clients.ptrans.Main
