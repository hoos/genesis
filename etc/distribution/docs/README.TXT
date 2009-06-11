================================================================================
Genesis Release Notes

================================================================================

VERSION: 0.0.34

Contents
--------

    1. License
    2. Overview
    3. Quick Start
    4. Contributors
    5. Contact Us

--------


1. License
----------

Genesis is licensed under the General Public License GPLv3
For more information consult: http://www.gnu.org/licenses/gpl.html


2. Overview
-----------

Genesis is an environment provisioning tool used to automate the configuration
of the enterprise stack.  Genesis can be used to configure everything from
network appliances such as switches and routers, server operating systems
and software application packages.  It offers a single, simple approach to
environment provisioning.

The three architectural elements within Genesis are:

1) Application Archetypes - Templates of application configurations,
   control scripts and other non binary application artefacts.

2) Genesis Confiuration Hierarchy - Configuration data maintained 
   in XML files that conform to the Genesis schema.

3) Genesis Engine - Combines application archetypes with your
   enterprise configuration to produce a deployable package of
   you application.


3. Quick Start
--------------

Make sure you have read INSTALL.TXT before proceeding.

Unpacking the genesis-dist-<version>.zip creates the following directory
structure:

GENESIS_ROOT/

archetypes	- these are templates of application configurations.
build.xml	- The build.xml contains the basic build instructions for Ant
config		- The Genesis enterprise configuration, stored in XML files.
contrib		- Third scripts and helper files contributed but not tested.
docs		- Guides and technical documentation.
engine		- The Genesis engine comtains Ant tasks and schemas. 
genesis		- A shell script used to execute Genesis for *NIX.
genesis.cmd	- A shell script used to execute Genesis for Windows. 
genesis.xml	- The genesis.xml file defines the configuration hierarchy
GPL		- The General Public License

Execute genesis without any arguments for help and usage:

[genesis]$ ./genesis

--> start of output

================================================================================

                               WELCOME TO GENESIS

================================================================================
VERSION: 0.0.34

Release: Light
License: GPL Version 3
Website: http://www.genesis.uk.com
Contact: info@genesis.uk.com

================================================================================
USAGE

Usage: genesis [target] [argument] [target2] [argument2] ... [argument8:build]

================================================================================
EXAMPLE

Usage: ./genesis -Denterprise=MyEnterprise -Dservice=MyService \
-Denvironment=MyEnvironment -Ddatacentre=MyDatacentre -Drack=MyRack \
-Dnode=MyNode -Dapplication=MyApplication MyApplication:build

<-- end of output

Executing genesis with the example shown in the command usage produces the 
following output.

./genesis -Denterprise=MyEnterprise -Dservice=MyService
-Denvironment=MyEnvironment -Ddatacentre=MyDatacentre -Drack=MyRack
-Dnode=MyNode -Dapplication=MyApplication MyApplication:build

--> start output

================================================================================

			       WELCOME TO GENESIS

================================================================================

VERSION: 0.0.34

Release: Light
License: GPL Version 3
Website: http://www.genesis.uk.com
Contact: info@genesis.uk.com

================================================================================

================================================================================

CHECKING JAVA ENVIRONMENT

JAVA_HOME = /opt/jdk

Java version:
java full version "1.6.0-b09"

Supported Java version: 
java full version "1.6.*"

Verify that your version of Java matches the supported version.


================================================================================

CHECKING ANT ENVIRONMENT

ANT_HOME = /opt/apache-ant

Ant version:
Apache Ant version 1.7.0 compiled on December 13 2006

Supported Ant version: 
Apache Ant version 1.7.0 compiled on December 13 2006

Verify that your version of Ant matches the supported version.

================================================================================

CHECKING TARGETS

SUCCESS: ALL TARGETS OK

-Denterprise -Dservice -Denvironment -Ddatacentre -Drack -Dnode -Dapplication :build

================================================================================

CHECKING ARGUMENTS

SUCCESS: ARGUMENT OK - MyEnterprise
SUCCESS: ARGUMENT OK - MyService
SUCCESS: ARGUMENT OK - MyEnvironment
SUCCESS: ARGUMENT OK - MyDatacentre
SUCCESS: ARGUMENT OK - MyRack
SUCCESS: ARGUMENT OK - MyNode
SUCCESS: ARGUMENT OK - MyApplication
SUCCESS: ARGUMENT OK - MyApplication

================================================================================
Buildfile: build.xml

validate-all:

init:

set-directory-locations:

MyApplication:build:
[genesisaction] About to execute action MyApplication with: 
[genesisaction]   /MyEnterprise (/Enterprise)
[genesisaction]   /MyEnterprise/MyDatacentre (/Enterprise/Datacentre)
[genesisaction]   /MyEnterprise/MyDatacentre/MyRack (/Enterprise/Datacentre/Rack)
[genesisaction]   /MyEnterprise/MyDatacentre/MyRack/MyNode (/Enterprise/Datacentre/Rack/Node)
[genesisaction]   /MyEnterprise/MyService (/Enterprise/Service)
[genesisaction]   /MyEnterprise/MyService/MyEnvironment (/Enterprise/Service/Environment)
[genesisaction]   /MyEnterprise/MyService/MyEnvironment/MyApplication (/Enterprise/Service/Environment/Application)

validate-parameters:

calculate-paths:

build-MyApplication:
    [mkdir] Created dir: /home/genesis/sandbox/projects/genesis/dist/src/main/root-files/build/MyEnterprise/MyService/MyEnvironment/MyDatacentre/MyRack/MyNode/MyApplication
     [copy] Copying 1 file to /home/genesis/sandbox/projects/genesis/dist/src/main/root-files/build/MyEnterprise/MyService/MyEnvironment/MyDatacentre/MyRack/MyNode/MyApplication

BUILD SUCCESSFUL
Total time: 1 second

================================================================================

GENESIS CONFIGURATION HIERARCHY

./build
`-- MyEnterprise
    `-- MyService
        `-- MyEnvironment
            `-- MyDatacentre
                `-- MyRack
                    `-- MyNode
                        `-- MyApplication
                            `-- MyApplication.cfg

7 directories, 1 file

================================================================================

<-- end of output

In our example we have provisionined provisioned a single application.  This
is how the Genesis Configuration Heirarchy looks like in the ./config
directory


config/
|-- MyEnterprise			<---------------Enterprise
|   |-- enterprise.xml
|   |-- logical				<---------------Logical Configuration
|   |   `-- MyService			<---------------Service
|   |       |-- MyEnvironment		<---------------Environment
|   |       |   |-- MyApplication	<---------------Application
|   |       |   |   `-- application.xml
|   |       |   `-- environment.xml
|   |       `-- service.xml
|   `-- physical			<---------------Physical Configuration
|       `-- MyDatacentre		<---------------Datacentre
|           |-- MyRack			<---------------Rack
|           |   |-- MyNode		<---------------Node
|           |   |   `-- node.xml
|           |   `-- rack.xml
|           `-- datacentre.xml
`-- common.xml

Try running the command and for yourself see what you find.


4. Contributors
---------------

The Genesis project would like to thank its many contributors. Without them 
the project would not have been nearly as successful as it has been. Any
omissions in this list are purely accidental.

Salim Badakhchani, Co-Founder and Project Maintainer
Hussein Badakhchani, Co-Founder and Project Maintainer
Derek Sozou, Web Designer/Developer
Steven Martin, Technical Consultant
Lalitkumar Dandekar, Developer and Technical Consultant
Mike Mochan, Lead Developer and Technical Consultant
Paul Jones, Developer
Raj Phani, Developer
Simon Vanscolina, Developer and Technical Consultant
Trevor Nielsen, Developer and Technical Consultant
Robert Smith, Developer
Sean Denson, Business Development Consultant

5. Contact Us
-------------

You can contact us at via:

Website: http://www.genesis.uk.com/contact
contact: info@genesis.uk.com
