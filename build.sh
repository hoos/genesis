#!/bin/sh

GENESIS_HOME=`/bin/env dirname $0`
ANT_HOME=$GENESIS_HOME/thirdparty/apache-ant-1.7.1

PATH=$ANT_HOME/bin:$PATH

ant $*
