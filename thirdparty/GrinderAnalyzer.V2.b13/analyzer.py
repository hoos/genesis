# Copyright (C) 2007-2011, Travis Bear
# All rights reserved.
#
# With contributions from
#    Angela Batiste
#    Marc Van Giel
#    Trilok Khairnar
#    Pedro Manuel
#    Antonio Manuel Muniz
#    Jason Laxdal
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

import classpathtool # sets the classpath for Java imports

# Java imports
from org.apache.log4j import *

from org.jfree.data.xy import XYSeriesCollection
from org.jfree.data.xy import DefaultTableXYDataset
from org.jfree.data.xy import XYSeries

from org.jtmb.grinderAnalyzer import ReportRow ###
from org.jtmb.grinderAnalyzer import Columns

# python imports
import sys

# Jython / analyzer imports
from ga.fileutils import tail
from ga.fileutils import reverseSeek
import ga.graph
import ga.report
import ga.bucket
import ga.constants



#####################################################################
# Manages the response time groups that were defined in config
#####################################################################
class ResponseTimeGroupHandler:
    _responseTimeGroups = {}
    '''
    key: transaction name
    value: dictionary
             key: max response time in this group in seconds (float).  If there
                  is no key lower than this one, the smallest response time in
                  this group is zero; otherwise it's the value of the next
                  lowest key.
             value: count (int) for times in this range.

    '''
    _txNumberNameMap = None # key: tx num, value: tx name
    _totalTxPassed = -1     # total for all transactions
    _totalTxPassMap = None  # key: tx name
                            # value: total passed tx's for all response time values
    _testNumColumn = ""
    
    def __init__(self, txNumNameMap):
        self._totalTxPassed = 0
        self._txNumberNameMap = txNumNameMap
        self._responseTimeGroups = {}
        self._totalTxPassMap = {}
        for txName in txNumNameMap.values():
            logger.debug("Creating timing group for %s" %txName)
            self._totalTxPassMap[txName] = 0
            _timeCountMap={}
            for maxTime in CONFIG.rtimeThresholds:
                _timeCountMap[maxTime] = 0
            self._responseTimeGroups[txName] = _timeCountMap

    def addRTGroupsToReport(self, analyzerPlugin):
        """
        The analyzer plugin has previously been set up with the summary data
        at the end of the grinder out_ file.  Here, we add the additional
        time group info to the existing report rows.
        
        """
        transactionNames=self._responseTimeGroups.keys()
        rtgroupNames = analyzerPlugin.getRtgroupColumnNames()
        logger.debug("--- %s" %rtgroupNames)
        logger.debug("KEYS %s" %transactionNames)
        reportRow = None
        for txName in transactionNames:
            if txName.strip() == ALL_TRANSACTIONS_VALUE:
                reportRow=analyzerPlugin.getTotalsRow()
            else:
                reportRow=analyzerPlugin.getRow(txName)
            logger.debug ("Adding '%s' data to row %s" %(txName, reportRow))
            timeGroups = self._responseTimeGroups[txName].keys()
            timeGroups.sort()
            logger.debug("Time groups: %s" %timeGroups)
            rtColumnIndex = 0
            for maxTime in timeGroups:
                groupMembers = float(self._responseTimeGroups[txName][maxTime])
                # catch the case where a non-decimal character in the summary causes
                # reportRow to return 0
                percent = "n/a"
                tests = float(reportRow.getColumnDataAsNum(Columns.TEST_PASSED))
                if tests > 0:
                    percent=groupMembers/tests
                    logger.debug("Group members: %d, tests: %d" %(groupMembers, tests))
                #logger.info("Max time: %f, members: %s, percent float: %d" %(maxTime, groupMembers, percent))
                logger.debug("adding tx name: %s, rt group: %s, percent, %s" %(txName, rtgroupNames[rtColumnIndex], str(percent)))
                reportRow.addNumericTransactionData(rtgroupNames[rtColumnIndex], percent)
                rtColumnIndex += 1
            if txName.strip() == ALL_TRANSACTIONS_VALUE:
                analyzerPlugin.setTotalsRow(reportRow)
            else:
                analyzerPlugin.updateRow(txName, reportRow)


    def __getKey__(self, rtime):
        """
        For a given response time, determine which response time group
        it belongs in.  This implementation depends on the rtime_thresholds
        having been pre-sorted
        """
        # skip the 'max_possible' key at the end of the list
        for key in CONFIG.rtimeThresholds[:len(CONFIG.rtimeThresholds)-1]:
            if rtime < key:
                return key
        # return 'max' if nothing else is found.
        return CONFIG.rtimeThresholds[len(CONFIG.rtimeThresholds)-1]

    def addData(self, columns):
        """
        This method will be called once for every line in the data_ file, which
        is potentially a large number.  Make it perform well.
        """
        if not CONFIG.useThresholds:
            return
        # bail if the transaction did not pass since Grinder records no
        # response time in that case
        if columns[ERRORS_COLUMN].strip() != "0":
            return
        rtimeSeconds=float(columns[RESPONSE_TIME_COLUMN])/1000.0
        key = self.__getKey__(rtimeSeconds)
        txName = self._txNumberNameMap[columns[TEST_NUMBER_COLUMN].strip()]
        #logger.info("Test %s rtime %f group %f" %(txName, rtimeSeconds, key))
        self._responseTimeGroups[txName][key] += 1
        self._responseTimeGroups[ALL_TRANSACTIONS_VALUE][key] += 1

    def getTxPercent(self, txName, maxTimeSec):
        txs = self._responseTimeGroups[txName][maxTimeSec]
        return txs / self._totalTxPassMap[txName]

    def printSummaryData(self):
        for txName in self._responseTimeGroups.keys():
            logger.debug("tx %s, time groups: %s" %(txName, self._responseTimeGroups[txName]))

    




#####################################################################
# ClientAnalyzer -- examines grinder logs
#####################################################################
class ClientLogAnalyzer:
    '''
    Generates JFreeChart Datasets for transactions per second, response
    time, and bandwidth utilization, based on the grinder agent logs.

    '''
    # class variables
    dataFiles = None
    outFile = None
    _agentMultiplier = None
    _txNameDatasets = None # maps transaction names to data sets
    bucketList = None
    msPerBucket = None
    maxElapsedTime = None
    _summaryData = None # maps transaction numbers to transaction names
    rtimeGroupHandler = None
    
    def __init__(self, dataFiles, summaryData, agents):
        self.dataFiles = dataFiles       
        self._summaryData = summaryData
        self._agentMultiplier = agents
        self._setTestStartTime_(dataFiles[0])
        self._setTestDuration_(dataFiles[0])
        self.rtimeGroupHandler = ResponseTimeGroupHandler(summaryData.getTxNumNameMap())
        self._analyzeLogs()
        self._txNameDatasets = None
        self.maxElapsedTime = long(0)
        logger.debug("DEBUG: config == null? " + str(CONFIG.buckets))

    def _setTestStartTime_(self, grinderDataFile):
        global TEST_START_TIME
        # as of Grinder 3.1, time column is absolute, rather than elapsed test time
        file=open(grinderDataFile)
        file.readline() # advance the pointer to line 2
        line2 = file.readline()
        TEST_START_TIME = long(line2.split( "," )[ELAPSED_TIME_COLUMN].strip())
        logger.debug("DEBUG: start time = " + str(TEST_START_TIME))

    def _setTestDuration_(self, grinderDataFile):
        ''' Determine the duration of the test by reading the timestamps 
        from the first and last lines of the data files. '''
        maxElapsedTime=long(0)
        elapsedTime = 0
        for grinderDataFile in self.dataFiles:
            # grinder data is not guaranteed to be in chronological order.
            # look at the last 200 lines to find the max elapsed time
            # value.
            for line in tail( grinderDataFile, 200, ignoreBlankLines=True ):
                # Trilok Khairnar's fix for data file containing < 200 lines
                elapsedTimeword = line.split( "," )[ELAPSED_TIME_COLUMN].strip()
                if elapsedTimeword.find("tart") < 0: # skip the header row for G 3.0 and 3.1
                    elapsedTime = long( elapsedTimeword) - TEST_START_TIME
                if elapsedTime > maxElapsedTime :
                    maxElapsedTime = elapsedTime
            self.msPerBucket=maxElapsedTime/CONFIG.buckets + 1 # round up
        #TODO -- make determining max time more bulletproof.  Perhaps detect file
        #        size and tail more lines for larger files.
        maxElapsedTime = long(maxElapsedTime * 1.00) # add a 1% safety factor
        self.maxElapsedTime = maxElapsedTime - TEST_START_TIME
        logger.debug("DEBUG: Max elapsed time == " + str(self.maxElapsedTime))        
        
    def _analyzeLogs(self):
        '''  Analyzes grinder agent logs.  Builds the list of buckets. '''
        # build an empty list of buckets       
        self.bucketList=[]
        for i in range(CONFIG.buckets+1):
            bucket = ga.bucket.getBucket(i * self.msPerBucket, (i+1) * self.msPerBucket, self)
            self.bucketList.append(bucket)

        # Read through the data logs to populate the buckets w/ scale data
        for dataFile in self.dataFiles :
            currentBucketIndex = 0;
            input=open( dataFile )
            line = input.readline() # throw away the first line w/ the headers
            line = input.readline() # now we have first "data" line
            while line != "":
                data = line.split( "," )
                elapsed_time_word = data[ELAPSED_TIME_COLUMN].strip() 
                elapsedTime = long(elapsed_time_word) - TEST_START_TIME
                # do we need a new bucket?
                # TODO -- what if one line is grossly out of chronological order?
                #         There's no logic to go back to the previous bucket
                while long( elapsedTime ) > self.bucketList[currentBucketIndex].endTime:
                    logger.debug("Bucket %d, elapsed time: %d, bucket end: %d" \
                                 %(currentBucketIndex, elapsedTime, self.bucketList[currentBucketIndex].endTime))
                    currentBucketIndex += 1
                # handle case where data file has fewer lines that configured buckets
                if currentBucketIndex < self.bucketList.__len__():
                    self.bucketList[currentBucketIndex].addLine(data)
                    self.rtimeGroupHandler.addData(data)
                line = input.readline()
            input.close()
        if CONFIG.isUseThresholds():
            self.rtimeGroupHandler.addRTGroupsToReport(VORPAL.getPlugin("analyzer"))
        self.rtimeGroupHandler.printSummaryData()
    
    def getAgentMultiplier(self):
        return self._agentMultiplier

    def getDataSets(self, txName):
        '''
        For a given txNum name, return a set of JFreeChart datasets
        with data on tx/sec, response times, and bandwidth.
        '''
        logger.debug("getting data sets for " + txName)
        if self._txNameDatasets == None:
            logger.warn("Building data sets.")
            txSecDataset = None
            # build 'em
            self._txNameDatasets = {}
            txNums = self._summaryData.getTxNumNameMap().keys()
            for txNum in txNums:
                logger.debug("DEBUG: building DS for " + txNum)
                dataSetGroup = {}
                txSecDataset = XYSeriesCollection() # not returning a new object
                bandwidthDataSet = XYSeriesCollection()
                simpleResponseTimeDataset = XYSeriesCollection()
                responseTimeDataset = DefaultTableXYDataset()
                txSecPassSeries = XYSeries("passed")
                txSecFailSeries = XYSeries("failed")
                responseTimeSeries = XYSeries("seconds")
                finishTimeSeries = XYSeries("complete", True, False)
                resolveHostSeries = XYSeries("resolveHost", True, False)
                connectSeries = XYSeries("connect", True, False)
                firstByteSeries = XYSeries("firstByte", True, False)
                bandwidthSeries = XYSeries("KB/sec")
                for bucket in self.bucketList:
                    txSecPass = bucket.getTxSecPass(txNum)
                    txSecPassSeries.add(bucket.getStartTime()/1000.0, txSecPass)
                    txSecFail = bucket.getTxSecFail(txNum)
                    txSecFailSeries.add(bucket.getStartTime()/1000.0, txSecFail)
                    responseTimeSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanResponseTime(txNum))
                    if ga.constants.VORPAL.getPlugin("analyzer").isHTTP():
                        bandwidthSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanThroughputKBSec(txNum))
                        finishTimeSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanFinishTime(txNum))
                        resolveHostSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanResolveHostTime(txNum))
                        connectSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanConnectTime(txNum))
                        firstByteSeries.add(bucket.getStartTime()/1000.0, bucket.getMeanFirstByteTime(txNum))
                txSecDataset.addSeries(txSecPassSeries)
                txSecDataset.addSeries(txSecFailSeries)
                responseTimeDataset.addSeries(resolveHostSeries)
                responseTimeDataset.addSeries(connectSeries)
                responseTimeDataset.addSeries(firstByteSeries)
                responseTimeDataset.addSeries(finishTimeSeries)
                simpleResponseTimeDataset.addSeries(responseTimeSeries)
                bandwidthDataSet.addSeries(bandwidthSeries)
                dataSetGroup[TX_SEC_KEY] = txSecDataset
                dataSetGroup[FULL_RESPONSE_TIME_KEY] = responseTimeDataset
                dataSetGroup[THROUGHPUT_KEY] = bandwidthDataSet
                dataSetGroup[SIMPLE_RESPONSE_TIME_KEY] = simpleResponseTimeDataset
                self._txNameDatasets[txNum] = dataSetGroup
            logger.debug("DEBUG: done building data sets.")
        return self._txNameDatasets[txName]

    def getTransactionMap(self):
        #logger.info("Num name map: %s" %self._summaryData.getTxNumNameMap())
        #logger.info("")
        return self._summaryData.getTxNumNameMap()



class SummaryDataRegistry:
    """
    Container for the summary data at the end of the grinder out_ file.
    
    """
    lineNameMap = None
    txNumNameMap = None
    
    def __init__(self, outFile):
        """
        Loads everything from the out file.  Filters out duplicate names.
        
        """
        self.txNumNameMap = {}
        self.lineNameMap = {} # key: data line, val: tx name
        finalOutLines = reverseSeek (outFile, TABLE_MARKER)
        for line in finalOutLines:
            if line.find( "Test" ) == 0 or line.find("Test") == 1 or line.startswith("Totals"):
                if line.startswith("Totals"):
                    # Format the totals line to be identical to test lines
                    line=line.replace("Totals", "Totals 0") # add a column
                    line='%s "%s"' %(line, ALL_TRANSACTIONS_VALUE)
                testName = self._getInitialTxName(line)
                self.lineNameMap [line] = testName
        if len(self.lineNameMap) == 0:
            msg = """
                FATAL:  Incomplete or corrupted grinder out file.  No summary data containing
                test number/name mappings found."""
            logger.fatal(msg)
            sys.exit(1)
            
        duplicateTxNames = self._getListDuplicates(self.lineNameMap.values())
        if len(duplicateTxNames) > 0:
            logger.info("Duplicate transaction names found: %s" %duplicateTxNames)
        for line in self.lineNameMap.keys():
            txName = self._getInitialTxName(line)
            txNum = self._getTxNum(line)
            if duplicateTxNames.__contains__(txName):
                txName ="%s_%s" %(txName, txNum)
            self.txNumNameMap[txNum] = txName
            self.lineNameMap[line] = txName
        logger.debug("Final tx names: %s" %self.txNumNameMap.values())


    def _getTxNum(self, line):
        return line.split( " " )[1] # test number is 2nd column
    
    def _getInitialTxName(self, line):
        return line.split( '"' )[1]   # test names appear in quotes
    
    def _getListDuplicates(self, list):
        """
        Returns a subset of list containing items that appear more than once
        
        """
        itemCountMap = {}
        for item in list:
            if itemCountMap.has_key(item):
                itemCountMap[item] += 1
            else:
                itemCountMap[item] = 1
        # list comprehension, w00t
        return [ key for key in itemCountMap.keys() if itemCountMap[key] > 1]
 
    def getTxNumNameMap(self):
        return self.txNumNameMap
    
    def getTestDataLines(self):
        return self.lineNameMap.keys()
    
    def getTxName(self, line):
        return self.lineNameMap[line]

    

#####################################################################
# End of class definitions.  Module logic below
#####################################################################

def usage():
    logger.fatal('Usage: analyzer.py "<grinder data Files>" <grinder out file> [number of agents]')
    sys.exit()
   


def assertCurrentJython():
    if SUPPORTED_JYTHON_VERSIONS.__contains__(sys.version.split()[0]):
        return
    logger.fatal("You are using Jython version " + sys.version + ".  Unfortunately,")
    logger.fatal("due to bugs in some Jython implementations, the only versions")
    logger.fatal( "of Jython that support Grinder Analyzer are:")
    for version in SUPPORTED_JYTHON_VERSIONS:
        logger.fatal("\t" + version)
    sys.exit()

def main():
    # validate the parameters
    if len( sys.argv ) < 3:
        usage()
    assertCurrentJython()
    logger.info(CONFIG)
    grinderData=sys.argv[1]
    grinderDataFiles=[]
    grinderOutFile=sys.argv[2]
    if len(grinderOutFile.split()) > 1:
        logger.fatal("FATAL: grinder analyzer only supports using a single out_* file")
        usage()
    agents=1
    if len( sys.argv )< 4:
        logger.warn("Optional TPS multiplier not set.  Using 1")
    else:
        agents = int( sys.argv[3] )

   
    # build the list of grinder data files specified on the command line
    for grinderDataFile in grinderData.strip().split(' '):
        if grinderDataFile is '' :
            continue
        if tail (grinderDataFile, CONFIG.buckets+1, ignoreBlankLines=True).__len__() < CONFIG.buckets :
            logger.fatal("")
            logger.fatal("FATAL: insufficient test data to graph.  conf/analyzer.properties specifies")
            logger.fatal( "       " + str(CONFIG.buckets) + " buckets, but " + grinderDataFile + " contains")
            logger.fatal( "       less than " + str(CONFIG.buckets) + " data points.")
            sys.exit(1)
        grinderDataFiles.append(grinderDataFile)
        if grinderDataFile.rfind( "data_" ) < 0 :
            logger.fatal( "Invalid grinder data file: " + grinderDataFile)
            usage()
    logger.info("Grinder data files specified: %d" %grinderDataFiles.__len__())
    if grinderOutFile.rfind( "out_" ) < 0:
        logger.fatal( "Invalid grinder out file: " + grinderOutFile)
        usage()
    summaryData = SummaryDataRegistry(grinderOutFile)
    
    # generate HTML report
    reporter = ga.report.getReporter(grinderOutFile, summaryData)
    reporter.readGrinderOutFile(summaryData)
  
    # generate the graphs
    analyzer = ClientLogAnalyzer(grinderDataFiles, summaryData, agents)
    transactions = analyzer.getTransactionMap()
    for transactionNumber in transactions.keys():
        datasets = analyzer.getDataSets(transactionNumber)
        transactionName = transactions[transactionNumber]
        perfDatasets = [datasets[TX_SEC_KEY],datasets[SIMPLE_RESPONSE_TIME_KEY]]
        perfGrapher = ga.graph.PerformanceGrapher(perfDatasets, transactionName, TEST_START_TIME)
        perfGrapher.saveChartToDisk(CONFIG.reportDir)  
        # BAIL HERE if non-http
        if not VORPAL.getPlugin("analyzer").isHTTP():
            continue
        bw = ga.graph.BandwidthGrapher([datasets[THROUGHPUT_KEY]], transactionName, TEST_START_TIME)
        bw.saveChartToDisk(CONFIG.reportDir)
        rtGrapher = ga.graph.ResponseTimeGrapher([datasets[FULL_RESPONSE_TIME_KEY]],
                                        transactionName,
                                        TEST_START_TIME)
        rtGrapher.saveChartToDisk(CONFIG.reportDir)
    reporter.writeReportToFile()
    logger.warn ("Log file analysis completed successfully.")



#####################################################################
# Control flow begins below
#####################################################################
# data_ file format info
ELAPSED_TIME_COLUMN=3      # elapsed time in ms since test began
TEST_NUMBER_COLUMN=2       # which test is this data for
RESPONSE_TIME_COLUMN=4     # no. of ms for server to respond
ERRORS_COLUMN=5            # did the transaction succeed?
BYTES_COLUMN=7             # if success, how many bytes in body?
RESOLVE_TIME_COLUMN=9      # 
CONNECT_TIME_COLUMN=10     #
FIRST_BYTE_TIME_COLUMN=11  #

# other constants
ALL_TRANSACTIONS_KEY="0"
ALL_TRANSACTIONS_VALUE="All Transactions"
TX_SEC_KEY="passed"
THROUGHPUT_KEY="kbSec"
SIMPLE_RESPONSE_TIME_KEY="response time"
FULL_RESPONSE_TIME_KEY="full time"
TABLE_MARKER="Final statistics for this process"

SUPPORTED_JYTHON_VERSIONS=["2.2.1", "2.5.0", "2.5.1"]


TEST_START_TIME=long(0)
MAX_POSSIBLE_TIME=99999999.9

logger = Logger.getLogger("analyzer")

# velocity merger and config classes instantiated in
# constants module to avoid creating duplicate instances
VORPAL= ga.constants.VORPAL
CONFIG = ga.constants.CONFIG

if __name__ == "__main__":
    main()
