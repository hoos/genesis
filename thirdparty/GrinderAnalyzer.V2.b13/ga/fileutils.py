# Copyright (C) 2007-2011, Travis Bear
# All rights reserved.
#
# With contributions from:
#    Ross Nicholson
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


import os
import sys
DEFAULT_MAX_LINES=3000



#####################################################################
# private function to reverse-read a single line in a file.  Code
# contributed by Ross Nicholson.
#
# Parameters:
#    input       an open file
#    position    the position in the file to begin reading from
#
# Returns:
#    a tuple containing the specified line of text and the
#    new position marker in the file.
#####################################################################
def __reverseRead__(input, position):
    line = ""
    position -= 1
    input.seek(position)
    ch = input.read(1)
    #print "> " + str(position) + "    '" + ch + "'" + "   " + str(ch == '\r') + "  " + str (ch == '\n')
    if ch != '\r' and ch != '\n':
        line = ch + line
    while ch != '\n' and position > 0:
        position -= 1
        input.seek(position)
        ch = input.read(1)
        if ch != '\r' and ch != '\n':
            line = ch + line
        #print str(position) + "    '" + ch + "'" + "   " + str(ch == '\r') + "  " + str (ch == '\n')
    #print "'" + line + "'   ==''? " + str(line == '') + ",  == CR? " + str (line == '\r') + ",  == NL? " + str(line=='\n')
    t = (line, position)
    return (line, position)



#####################################################################
# reverseSeek.
# New implementation to correct a windows compatibility issue
# raised by Ross Nicholson.  Now reads 1 character at a time in
# order to accurately detect line endings.  Slower, but reliable.
#####################################################################
def __reverse__ (filename, target="", maxLines=DEFAULT_MAX_LINES, ignoreBlank=False):
    # handle the case where the file does not exist
    #print "Default max = " + str(DEFAULT_MAX_LINES) + ", Max lines = " + str(maxLines) + ", target='" + target + "'"
    if not os.path.isfile(filename):
        print "FATAL: not a file: '" + filename + "'.  No action performed."
        sys.exit(1)      
    fileSize = os.stat(filename)[6]
    # handle the zero-length file special case   
    if fileSize == 0:
        print "WARNING: empty file '" + filename + "'.  No action performed."
        return []
    # TODO -- handle the case where there is no read access on the file
    lines=[] 
    search = True
    if target == "":
        search = False
    input=open(filename)
    position = fileSize -1
    # front-load the while loop
    lineData = __reverseRead__(input, position)
    linesRead=1
    line = lineData[0]
    position = lineData[1]
    if ignoreBlank==False or line !='':
        lines = [lineData[0]]
    if search and line.find(target) > -1:
        print "'" + target + "' found after searching back " + str(linesRead) + " lines."
        return lines
    # either read all the way back to the beginning of the file, or
    # read back to maxLines, whichever comes first
    while position > 0 and linesRead < maxLines:
        lineData = __reverseRead__(input, position)
        line = lineData[0]
        position = lineData[1]
        if ignoreBlank==False or line != '':
            linesRead += 1
            lines = [line] + lines
        if search and line.find(target) > -1:
            print "'" + target + "' found after searching back " + str(linesRead) + " lines."
            return lines
    if search:
        print "'" + target + "' not found in final " + str(linesRead) + " lines of " + filename
        return []
    return lines



#####################################################################
# reverse seek
#####################################################################
def reverseSeek(file, target, max=DEFAULT_MAX_LINES, ignoreBlankLines=False):
    '''
    Starts at the end of a file and seeks backwards until a target string 
    is found.  Assumes sufficient permission to read the file.
    
    Parameters:
        filename    File to inspect
        target      String to search for
        maxlines    (optional) how many lines to inspect before giving up
    
    Returns:
        a list of the final lines of the file, starting from the line
        containing the target string,  Returns an empty list if the
        target is not found.
    '''
    return __reverse__(file, target, max, ignoreBlankLines)



#####################################################################
# tail
#####################################################################
def tail(file, lines, ignoreBlankLines=False):
    '''
    Starts at the end of a file and seeks backwards until a target string 
    is found.  Assumes sufficient permission to read the file.
    
    Parameters:
        filename    File to tail
        lines       how many lines to return
    
    Returns:
        a list of the final lines of the file, starting from the line
        containing the target string,  Returns an empty list if the
        target is not found.
    '''
    return __reverse__(file, maxLines=lines, ignoreBlank=ignoreBlankLines)
