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

# GenesisLoaderTest tests
test1 = Test(1, "Test Load Handles Valid File With Empty Model")
test2 = Test(2, "Test Load Throws File Not Found Exception For Empty XML")
test3 = Test(3, "Test Load Rejects Invalid Model Reader")
test4 = Test(4, "Test Load Creates Valid Model Reader Instance")
test5 = Test(5, "Test Load Configures Configurable Model Reader Instance")
test6 = Test(6, "Test Provides Action Factory For No Actions")
test7 = Test(7, "Test Provides Action Factory For Actions")

# ArchetypeFactoryImplTest tests
test8 = Test(8, "Test Factory Returns Correct Item Count")

class TestRunner:
    def __call__(self):
       # GenesisLoaderTest
       genesisLoaderTest = GenesisLoaderTest()
       genesisLoaderTest.setUp()
       # Test 1
       testWrapper = test1.wrap(genesisLoaderTest)
       testWrapper.testLoadHandlesValidFileWithEmptyModel();
       # Test 2
       testWrapper = test2.wrap(genesisLoaderTest)
       testWrapper.testLoadThrowsFileNotFoundExceptionForEmptyXml();
       # Test 3
       testWrapper = test3.wrap(genesisLoaderTest)
       testWrapper.testLoadRejectsInvalidModelReader();
       # Test 4
       testWrapper = test4.wrap(genesisLoaderTest)
       testWrapper.testLoadCreatesValidModelReaderInstance();
       # Test 5
       testWrapper = test5.wrap(genesisLoaderTest)
       testWrapper.testLoadConfiguresConfigurableModelReaderInstance();
       # Test 6
       testWrapper = test6.wrap(genesisLoaderTest)
       testWrapper.testProvidesActionFactoryForNoActions();
       # Test 7
       testWrapper = test7.wrap(genesisLoaderTest)
       testWrapper.testProvidesActionFactoryForActions();

       # ArchetypeFactoryImplTest
       archetypeFactoryImplTest = ArchetypeFactoryImplTest()
       #archetypeFactoryImplTest.setUp()
       # Test 8
       testWrapper = test8.wrap(archetypeFactoryImplTest)
       testWrapper.testFactoryReturnsCorrectItemCount();
