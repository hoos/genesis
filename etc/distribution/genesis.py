# VGLUE HTTP test script.
#
# A simple example using the HTTP plugin that shows the retrieval of a
# single page via HTTP. The resulting page is written to a file.
#

from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from com.uk.genesis import *
from com.uk.genesis.action import *
from com.uk.genesis.ant import *
from com.uk.genesis.model import *

test0 = Test(0, "Test Load Handles Valid File With Empty Model")

class TestRunner:
    def __call__(self):
       genesisLoaderTest = GenesisLoaderTest()
       genesisLoaderTest.setUp()
       request = test0.wrap(genesisLoaderTest)
       result = request.testLoadHandlesValidFileWithEmptyModel();

