# XML-RPC Web Service
#
# A server should be running on the localhost. This script uses the
# example from
# http://xmlrpc-c.sourceforge.net/xmlrpc-howto/xmlrpc-howto-java-server.html
#
# Copyright (C) 2004 Sebastián Fontana
# Distributed under the terms of The Grinder license.

from java.util import Vector
from java.lang import Integer
from net.grinder.script.Grinder import grinder
from net.grinder.script import Test

from org.apache.xmlrpc import XmlRpcClient

test1 = Test(1, "XML-RPC example test")
server_url = "http://localhost:8080/RPC2"

serverWrapper = test1.wrap(XmlRpcClient(server_url))

class TestRunner:
    def __call__(self):
        params = Vector()
        params.addElement(Integer(6))
        params.addElement(Integer(3))

        result = serverWrapper.execute("sample.sumAndDifference", params)
        sum = result.get("sum")

        grinder.logger.output("SUM %d" % sum)
