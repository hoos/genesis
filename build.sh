#!/bin/sh
#
# build.sh     A build script that wraps Ant.
#
# Author:      Hussein Badakhchani
#
# Description: This script is used to compile and create distributions of
#              Genesis.  All the arguments that are supplied to this script
#              are passed on to Ant.

GENESIS_HOME=`/bin/env dirname $0`
ANT_HOME=$GENESIS_HOME/thirdparty/apache-ant-1.7.1

PATH=$ANT_HOME/bin:$PATH

ant $*
