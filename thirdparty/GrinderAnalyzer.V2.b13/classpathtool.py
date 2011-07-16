# Copyright (C) 2008-2011, Travis Bear
# All rights reserved.
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
#
# 
#   DESCRIPTION:  Helper script used by the diff tool and grinder analyzer to
#                 set the java classpath and validate run location.
#        AUTHOR:  Travis Bear 
#       CREATED:  10/23/2008 17:31:24


import sys
import os

# Ensure we're on jython and not cpython
if os.name != 'java':
    print "FATAL: cpython is not supported. This program must be invoked with jython 2.2.1 or later."
    sys.exit(1)


# Try to find the grinder analyzer dir.
GA_NAME="GrinderAnalyzer"
fullCurrentDir=os.path.realpath(os.path.curdir)
cdir=os.path.split(fullCurrentDir)[1]
if cdir.find(GA_NAME) != 0:
#    if not fullCurrentDir.endswith("grinderAnalyzer/scripts"):
        print "FATAL: This program can only be run from within the %s directory." %GA_NAME
        print "Current dir: %s, cdir: %s" %(os.getcwd(),cdir)
        sys.exit(1)

# Set the classpath
libDir="lib"
jars=os.listdir(libDir)
for jar in jars:
   sys.path.insert(0,libDir + os.sep + jar)


