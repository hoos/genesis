# Thread ramp up
#
# A simple way to start threads at different times.
#

from net.grinder.script.Grinder import grinder
from net.grinder.common import Logger

def log(message):
    """Log to the console, the message will include the thread ID"""
    grinder.logger.output(message, Logger.TERMINAL)

class TestRunner:
    def __init__(self):
        log("initialising")

    def initialSleep( self):
        sleepTime = grinder.threadNumber * 5000  # 5 seconds per thread
        grinder.sleep(sleepTime, 0)
        log("initial sleep complete, slept for around %d ms" % sleepTime)

    def __call__( self ):
        if grinder.runNumber == 0: self.initialSleep()

        grinder.sleep(500)
        log("in __call__()")
