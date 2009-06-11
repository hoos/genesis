package com.uk.genesis.model;

import java.io.File;

import junit.framework.TestCase;

public class ObjectFinderTest extends TestCase {
	public void testFindFiles() throws Exception {
		String[] result = ObjectFinder.findObjectNames(new File("etc/testdata/config/MyEnterprise-00/physical/MyDatacentre-00"), 
				"${name}", "rack.xml");
		
		assertEquals(2, result.length);
		assertEquals("MyRack-00", result[0]);
		assertEquals("MyRack-01", result[1]);
	}
}
