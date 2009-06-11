package com.uk.genesis.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import junit.framework.TestCase;

public class ValueLocatorTest extends TestCase {
	public void testValidatesTypeIsSet() {
		ValueLocator locator = new ValueLocator();
		locator.setProject(new Project());
		locator.setGenesisxml("etc/testdata/genesis.xml");
		locator.setProperty("Environment.Test");
		
		try {
			locator.execute();
			fail("Should have checked type attribute");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("type"));
		}
	}
	
	public void testValidatesPropertyIsSet() {
		ValueLocator locator = new ValueLocator();
		locator.setProject(new Project());
		locator.setGenesisxml("etc/testdata/genesis.xml");
		locator.setType("/Enterprise/Service/Environment");
		
		try {
			locator.execute();
			fail("Should have checked property attribute");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("property"));
		}
	}
	
	public void testValidatesType() {
		ValueLocator locator = new ValueLocator();
		locator.setProject(new Project());
		locator.setGenesisxml("etc/testdata/genesis.xml");
		locator.setType("/Enterprise/Service/Environment-Invalid");
		locator.setProperty("Environment.Name");
		
		try {
			locator.execute();
			fail("Should have failed on broken type");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("/Enterprise/Service/Environment-Invalid") &&
					ex.getMessage().contains("invalid"));
		}
	}
}
