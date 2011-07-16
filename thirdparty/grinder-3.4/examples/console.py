# Test script which generates some random data for testing the
# console.

from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from java.util import Random
from java.lang import Math

r = Random()

def doIt(v):
    t = 500 + r.nextGaussian() * v * 10
    grinder.sleep(int(t), 0)
    pass

tests = [ Test(i, "Test %s" % i).wrap(doIt) for i in range(0, 10) ]

class TestRunner:
    def __call__(self):
        statistics = grinder.statistics

#        statistics.delayReports = 1

        for test in tests:
            test(test.__test__.number)


