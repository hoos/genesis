# Copyright (C) 2011, Travis Bear
# All rights reserved.
#
# In addition to the individuals named above, several anonymous users
# have contributed as well.
#
# This file is part of Grinder Analyzer.
#
# Grinder Analyzer is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# Grinder Analyzer is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Grinder Analyzer; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

from org.jtmb.grinderAnalyzer import Configuration
from org.jtmb.velocityMerger import VelocityMerger
from org.apache.log4j import *
from java.util import Properties


def _getProperties_():
    # vPlugin.analyzer=org.jtmb.grinderAnalyzer.HTTPTestPlugin
    props = CONFIG.startupProperties
    props.put("vPlugin.analyzer", "org.jtmb.grinderAnalyzer.GAVelocityPlugin")
    return props


CONFIG_FILE="conf/analyzer.properties"


# Log4j must be configured before the VelocityMerger classes
# are instantiated.
PropertyConfigurator.configure(CONFIG_FILE)
CONFIG=Configuration(CONFIG_FILE)
VORPAL=VelocityMerger(_getProperties_())
