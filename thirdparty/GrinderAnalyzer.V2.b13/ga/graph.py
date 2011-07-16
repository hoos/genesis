# Copyright (C) 2007-2011, Travis Bear
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


from org.jfree.chart.plot import *
from org.jfree.chart.title import *
from org.jfree.chart.axis import *
from org.jfree.data.xy import XYSeries
from org.jfree.data.xy import XYSeriesCollection
from org.jfree.data.xy import DefaultTableXYDataset
from org.jfree.chart.renderer.xy import StandardXYItemRenderer
from org.jfree.chart.renderer.xy import StackedXYAreaRenderer2
from org.jfree.chart.axis import NumberAxis
from org.jfree.chart.plot import CombinedDomainXYPlot
from org.jfree.chart import JFreeChart
from org.jfree.chart import ChartUtilities
from org.jfree.chart import ChartFactory

from java.awt import Color as javaColor
from java.io import File as javaFile
from java.text import SimpleDateFormat as javaSimpleDateFormat
from java.util import Date as javaDate

from org.apache.log4j import *

import os
import ga.constants

#####################################################################
# Generic superclass with most of the concrete logic
#####################################################################
class AbstractGrapher(object):
    ''' Abstract superclass of various graphing classes '''
    chart = None
    chart_width=None
    chart_height=None
    dataSets = None
    transactionName = None
    startTime = None
    
    def __init__(self, dataSets, txName, startTime):
        self.dataSets = dataSets
        self.transactionName = txName
        self._createChart()
        self.startTime = startTime
        logger.debug("DEBUG: title: " + self.getTitle() + ", ds: " + str(self.dataSets))

    def getTitle(self):
        return self.transactionName
    
    def getXAxisLabel(self):
        return "Elapsed time, seconds"
    
    def getYAxisLabel(self):
        return ""
    
    def getFilenameSuffix(self):
        raise NotImplementedError
    
    def getSubtitles(self):
        ''' must return a list of chart subtitles (as strings) '''
        raise NotImplementedError
    
    def _createChart(self):
        self.chart = ChartFactory.createXYLineChart(
            self.getTitle(),          # chart title
            self.getXAxisLabel(),     # x axis label
            self.getYAxisLabel(),     # y axis label
            self.dataSets[0],         # data (primary)
            PlotOrientation.VERTICAL,
            True,                     # include legend
            False,                    # tooltips
            False                     # urls
        )


    def setChartOptions(self):
        ''' Many useful options are set here.  Concrete implementations
        of AbstractGrapher may wish to call this superclass method directly,
        in addition to any specific options they set themselves. '''
        self.chart.setAntiAlias(False)
        self.chart.setBackgroundPaint(javaColor.WHITE)
        #self.chart.setBorderVisible(True)
        for subtitle in self.getSubtitles():
            self.chart.addSubtitle(TextTitle(subtitle))
        if CONFIG.showDate:
            if self.startTime > 1000000000000: # is grinder version > 3.0 ?
                dateFormat=javaSimpleDateFormat(CONFIG.dateFormat)
                date=dateFormat.format(javaDate(self.startTime))
                self.chart.addSubtitle(TextTitle(date))
            else:
                logger.warn("   WARNING: Grinder Analyzer does not support adding dates to graphs generated from Grinder 3.0-format log files.")

    
    def saveChartToDisk(self, basedir):
        ''' Writes the chart to disk as a .png file in the specified
        directory. '''
        fileName = basedir + os.sep
        txName=self.transactionName 
        # fix for Jim Pringle's bug where illegal chars in transaction name
        # need a swap variable because replace() works inline
        fileName += txName.replace(" ", "_").replace("/", "_").replace(":", "_")
        fileName += self.getFilenameSuffix()
        logger.warn("creating " + fileName)
        chartFile = javaFile(fileName) # translate string to java.io.File
        self.setChartOptions()
        logger.debug("DEBUG: height"  + str(self.chart_height))
        logger.debug("DEBUG: weight" + str(self.chart_width))
        ChartUtilities.saveChartAsPNG(
              chartFile, 
              self.chart,
              self.chart_width,
              self.chart_height
        )


#####################################################################
#
#####################################################################
class PerformanceGrapher(AbstractGrapher):

    chart_height = None
    chart_width = None
    
    def __init__(self, dataSets, txName, startTime):
        AbstractGrapher.__init__(self, dataSets, txName, startTime)
        self.chart_height = CONFIG.tpsChartHeight
        self.chart_width = CONFIG.tpsChartWidth
        #self.startTime = startTime

    def _createChart(self):

        # create subplot 1...
        tpsDataset = self.dataSets[0]
        passPerSecRenderer = StandardXYItemRenderer()
        tpsRangeAxis = NumberAxis("Transactions per second")
        tpsSubplot = XYPlot(tpsDataset, None, tpsRangeAxis, passPerSecRenderer);
        tpsSubplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT)
        
        # create subplot 2...
        rtimeDataset = self.dataSets[1]
        rtimeRenderer = StandardXYItemRenderer()
        rtimeRangeAxis = NumberAxis("Response Time")
        rtimeRangeAxis.setAutoRangeIncludesZero(False)
        rtimeSubplot = XYPlot(rtimeDataset, None, rtimeRangeAxis, rtimeRenderer)
        rtimeSubplot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT)

        # parent plot...
        plot = CombinedDomainXYPlot(NumberAxis(self.getXAxisLabel()))
        plot.setGap(10.0)

        # passed per sec
        # RGB for dark green
        passPerSecRenderer.setSeriesPaint(0, COLOR_TXSEC_PASS)
        passPerSecRenderer.setSeriesPaint(1, COLOR_TXSEC_FAIL)
        passPerSecRenderer.setBaseShapesVisible(False)

        # response time
        rtimeRenderer.setSeriesPaint(0, COLOR_RTIME)
        rtimeRenderer.setBaseShapesVisible(False)
        # failed per sec
        failPerSecRenderer = StandardXYItemRenderer()
        failPerSecRenderer.setSeriesPaint(0, javaColor.red)
        failPerSecRenderer.setBaseShapesVisible(False)
        tpsSubplot.setRenderer(0, passPerSecRenderer)
        tpsSubplot.setRenderer(1, failPerSecRenderer)
        rtimeSubplot.setRenderer(0, rtimeRenderer)

        # add the subplots.  TPS plot gets more weight,
        # making it proportionally larger.
        plot.add(tpsSubplot, CONFIG.tpsWeight)
        plot.add(rtimeSubplot, CONFIG.responseTimeWeight)
        plot.setOrientation(PlotOrientation.VERTICAL)

        # create a new chart containing the overlaid plot...
        self.chart = JFreeChart(self.getTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, True)

    
    def getSubtitles(self):
        return ["Performance"]
    
    def getFilenameSuffix(self):
        return ".perf.png"





#####################################################################
#
#####################################################################
class BandwidthGrapher(AbstractGrapher):

    chart_height = None
    chart_width = None
    
    def __init__(self, dataSets, txName, startTime):
        AbstractGrapher.__init__(self, dataSets, txName, startTime)
        self.chart_height = CONFIG.bwChartHeight
        self.chart_width = CONFIG.bwChartWidth
        #self.startTime = startTime

    def getSubtitles(self):
        ''' must return a list of chart subtitles (as strings) '''
        return ["Bandwidth Used"]
    
    def getFilenameSuffix(self):
        return ".bandwidth.png"



#####################################################################
#
#####################################################################

class ResponseTimeGrapher(AbstractGrapher):

    chart_height = None
    chart_width = None

    def __init__(self, dataSets, txName, startTime):
        AbstractGrapher.__init__(self, dataSets, txName, startTime)
        self.chart_height = CONFIG.rtChartHeight
        self.chart_width = CONFIG.rtChartWidth
        #self.startTime = startTime

    def getSubtitles(self):
        #must return a list of chart subtitles (as strings)
        return ["Response Time"]

    def getFilenameSuffix(self):
        return ".rtime.png"

    def getYAxisLabel(self):
        return "response time, seconds"
    
    def _createChart(self):
        # create the plot
        rtimeDataset = self.dataSets[0]
        rtimeRenderer = StackedXYAreaRenderer2()
        rtimeRangeAxis = NumberAxis(self.getYAxisLabel())
        rtimeRangeAxis.setAutoRangeIncludesZero(False)
        rtimeDomainAxis = NumberAxis(self.getXAxisLabel())
        #rtimePlot = XYPlot(rtimeDataset, None, rtimeRangeAxis, rtimeRenderer)
        rtimePlot = XYPlot(rtimeDataset, rtimeDomainAxis, rtimeRangeAxis, rtimeRenderer)
        rtimePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT)

        # response time
        rtimeRenderer.setSeriesPaint(0, COLOR_RTIME_RESOLVE)
        rtimeRenderer.setSeriesPaint(1, COLOR_RTIME_CONNECT)
        rtimeRenderer.setSeriesPaint(2, COLOR_RTIME_FIRSTBYTE)
        rtimeRenderer.setSeriesPaint(3, COLOR_RTIME_FINAL)

        #rtimeRenderer.setBaseShapesVisible(False)
        #rtimePlot.setRenderer(0, rtimeRenderer)
        rtimePlot.setRenderer(rtimeRenderer)

        # create a new chart containing the plot...
        self.chart = JFreeChart(self.getTitle(),
                                JFreeChart.DEFAULT_TITLE_FONT,
                                rtimePlot,
                                True)

# Module constants

COLOR_TXSEC_PASS=javaColor.green.darker()
COLOR_TXSEC_FAIL=javaColor.red
COLOR_RTIME=javaColor.blue
COLOR_RTIME_RESOLVE=javaColor.red
COLOR_RTIME_CONNECT=javaColor.yellow
COLOR_RTIME_FIRSTBYTE=javaColor.orange
COLOR_RTIME_FINAL=javaColor.blue
CONFIG=ga.constants.CONFIG
logger = Logger.getLogger("ga.graph")
