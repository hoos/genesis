package com.uk.genesis.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import junit.framework.TestCase;

public class RetrieveObjectPropertyTest extends TestCase {
	public void testValidatesTypeParameter() {
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(new Project());
		task.setGenesisxml("etc/testdata/genesis.xml");
//		task.setType("/Enterprise/Datacentre");
		task.setName("/MyEnterprise-00/MyDatacentre-00");
		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have checked type property");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("type"));
		}
	}
	
	public void testValidatesNameParameter() {
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(new Project());
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre");
//		task.setName("/MyEnterprise-00/MyDatacentre-00");
		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have checked name property");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("name"));
		}
	}
	
	public void testValidatesPropertyParameter() {
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(new Project());
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre");
		task.setName("/MyEnterprise-00/MyDatacentre-00");
//		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have checked property property");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("property"));
		}
	}
	
	public void testValidatesTokenParameter() {
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(new Project());
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre");
		task.setName("/MyEnterprise-00/MyDatacentre-00");
		task.setProperty("DatacentreName");
//		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have checked token property");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("token"));
		}
	}
	
	public void testValidatesType() {
		Project project = new Project();
		
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(project);
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre-Invalid");
		task.setName("/MyEnterprise-00/MyDatacentre-00");
		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have thrown error on invalid type");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("/Enterprise/Datacentre-Invalid"));
			assertTrue(ex.getMessage().contains("invalid"));
		}
	}
	
	public void testValidatesName() {
		Project project = new Project();
		
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(project);
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre");
		task.setName("/MyEnterprise-00/MyDatacentre-00XYZ");
		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		try {
			task.execute();
			fail("Should have thrown error on invalid name");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("/Enterprise/Datacentre"));
			assertTrue(ex.getMessage().contains("/MyEnterprise-00/MyDatacentre-00XYZ"));
			assertTrue(ex.getMessage().contains("not found"));
		}
	}
	
	public void testSetsPropertyOnCompletion() {
		Project project = new Project();
		
		RetrieveObjectProperty task = new RetrieveObjectProperty();
		task.setProject(project);
		task.setGenesisxml("etc/testdata/genesis.xml");
		task.setType("/Enterprise/Datacentre");
		task.setName("/MyEnterprise-00/MyDatacentre-00");
		task.setProperty("DatacentreName");
		task.setToken("Datacentre.Name");
		
		task.execute();
		assertEquals("MyDatacentre-00", project.getProperty("DatacentreName"));
	}
}
