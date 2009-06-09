================================================================================
Genesis Developer Guide

================================================================================

VERSION: 0.0.34


Contents
--------

1. Getting Started with Genesis Source
2. Building a Distribution


1. Getting Started with Genesis Source
--------------------------------------

The Genesis Source uses Maven 2 (http://maven.apache.org) for generating a
usable Genesis installation.  Note however that Maven is not required to use
Genesis. To install Maven, follow the instructions available at
http://maven.apache.org/download.html.

The Genesis source is arranged into a number of modules. Currently, these
modules are:

GENESIS_SRC_ROOT/
core-ant-tasks/
dist/
model/
schemas/

CORE-ANT-TASKS
This module contains the core Genesis ant tasks, such as those for resolving
genesis objects to file names, building filter sets, and iterating of Genesis
objects.

DIST
This module contains all the sample files packaged into the distribution, along
with all of the packaging configuration to produce the genesis-dist.zip file in
the correct layout.

MODEL
This module contains all the code necessary to work with genesis.xml 
configuration files. It defines the underlying object model for Genesis, 
along with the mechanisms to load and query them.

SCHEMAS
This module contains the definitions for the XML schemas relevant to the 
genesis core, such as the schema for the genesis.xml file.

2. Building a Distribution
--------------------------

To generate a distributable Genesis binary, execute:

mvn package

You must be in the GENESIS_SRC_ROOT directory. This will download all necessary
dependencies, and build each of the modules.

The output distributable can be found in the dist\target directory as a .zip
file.
