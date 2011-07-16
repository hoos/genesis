# Enterprise Java Beans
#
# Exercise a stateful session EJB from the BEA WebLogic Server 7.0
# examples. Additionally this script demonstrates the use of the
# ScriptContext sleep(), getThreadId() and getRunNumber() methods.
#
# Before running this example you will need to add the EJB client
# classes to your CLASSPATH.

from java.lang import String
from java.util import Properties,Random
from javax.naming import Context,InitialContext
from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from weblogic.jndi import WLInitialContextFactory

tests = {
    "home" : Test(1, "TraderHome"),
    "trade" : Test(2, "Trader buy/sell"),
    "query" : Test(3, "Trader getBalance"),
    }

# Initial context lookup for EJB home.
p = Properties()
p[Context.INITIAL_CONTEXT_FACTORY] = WLInitialContextFactory.name

home = InitialContext(p).lookup("ejb20-statefulSession-TraderHome")
homeTest = tests["home"].wrap(home)

random = Random()

class TestRunner:
    def __call__(self):
        log = grinder.logger.output

        trader = homeTest.create()

        tradeTest = tests["trade"].wrap(trader)

        stocksToSell = { "BEAS" : 100, "MSFT" : 999 }
        for stock, amount in stocksToSell.items():
            tradeResult = tradeTest.sell("John", stock, amount)
            log("Result of tradeTest.sell(): %s" % tradeResult)

        grinder.sleep(100)              # Idle a while

        stocksToBuy = { "BEAS" : abs(random.nextInt()) % 1000 }
        for stock, amount in stocksToBuy.items():
            tradeResult = tradeTest.buy("Phil", stock, amount)
            log("Result of tradeTest.buy(): %s" % tradeResult)

        queryTest = tests["query"].wrap(trader)
        balance = queryTest.getBalance()
        log("Balance is $%.2f" % balance)

        trader.remove()                 # We don't record the remove() as a test

        # Can obtain information about the thread context...
        if grinder.threadNumber == 0 and grinder.runNumber == 0:
            # ...and navigate from the proxy back to the test
            d = queryTest.__test__
            log("Query test is test %d, (%s)" % (d.number, d.description))

