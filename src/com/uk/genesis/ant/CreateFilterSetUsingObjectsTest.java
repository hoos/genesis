package com.uk.genesis.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FilterSet;

import junit.framework.TestCase;

public class CreateFilterSetUsingObjectsTest extends TestCase {
	public void testAddConfiguredObjectAcceptsValidObject() {
		GenesisObjectReference ref = new GenesisObjectReference();
		
		ref.setType("Enterprise/Datacentre/Rack/Node");
                ref.setName("/Object/Test");
		
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		task.init();
		task.addConfiguredObject(ref);
	}
	
    /**
     * If the name is not set on the GenesisObjectReference when it is
     * added to the task a Build Exception should be thrown.
     */
	public void testAddConfiguredObjectRejectsMissingName() {
		GenesisObjectReference ref = new GenesisObjectReference();
		
		ref.setType("Enterprise/Datacentre/Rack/Node");
                //ref.setName("/Object/Test");
		try {
		        CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
                        task.init();
			task.addConfiguredObject(ref);
			fail("Should have thrown BuildException");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("name"));
		}
	}
	
        /**
         * If the type is not set on the GenesisObjectReference when it is
         * added to the task a Build Exception should be thrown.
         */
	public void testAddConfiguredObjectRejectsMissingType() {
		GenesisObjectReference ref = new GenesisObjectReference();
		
		//ref.setType("Enterprise/Datacentre/Rack");
                ref.setName("MyEnterprise-00/MyDatacentre-00/MyRack-00/MyNode-00");

		try {
		        CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
                        task.init();
			task.addConfiguredObject(ref);
			fail("Should have thrown BuildException");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("type"));
		}
	}
	
	public void testExecuteRequiresFilterSetId() {
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		Project project = new Project();
		task.init();
		task.setProject(project);
		task.setGenesisxml("genesis.xml");
		
		try {
			task.execute();
			fail("Should have thrown BuildException");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("filtersetid"));
		}
	}
	
        /**
         * H.B This task is actually testing for a missing attribute
         * in the task which is normally set by a call:
         * task.setGenesisxml("./src/test/etc/genesis.xml");
         *  
         */
	public void testThrowsBuildExceptionForMissingGenesisXml() {
		Project project = new Project();
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		task.setProject(project);
		task.init();
	        task.setFiltersetid("my.filters");
		
		try {
                        task.execute();
			fail("Should have thrown BuildException");
		} catch (BuildException ex) {
			assertTrue(ex.getMessage().contains("genesisxml") && ex.getMessage().contains("required"));
		}
	}
	
	public void testExecuteAddsFilterSetToProject() {
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		Project project = new Project();
		task.init();
		task.setProject(project);
		task.setFiltersetid("testset");
		task.setGenesisxml("etc/testdata/genesis.xml");
		
		task.execute();
		
		assertNotNull(project.getReference("testset"));
		assertTrue(project.getReference("testset") instanceof FilterSet);
	}
	
	public void testAddedFiltersetHasHierarchyOfElements() {
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		Project project = new Project();
		task.init();
		task.setProject(project);
		task.setFiltersetid("testset");
		task.setGenesisxml("etc/testdata/genesis.xml");
		
		GenesisObjectReference ref = new GenesisObjectReference();
		ref.setType("Enterprise/Datacentre/Rack/Node");
		ref.setName("MyEnterprise-00/MyDatacentre-00/MyRack-00/MyNode-00");
		task.addConfiguredObject(ref);
		
                try {
		task.execute();
		
		FilterSet filters = (FilterSet) project.getReference("testset");
                //System.out.println(filters.replaceTokens("@Enterprise.Name@ @Datacentre.Name@ @Rack.Name@"));
		assertEquals("MyEnterprise-00 MyDatacentre-00 MyRack-00 MyNode-00", filters.replaceTokens("@Enterprise.Name@ @Datacentre.Name@ @Rack.Name@ @Node.Name@"));
                } catch (Exception e) {
                  e.printStackTrace();
                }
	}
	
	public void testAddedFiltersetHasHierarchyOfElementsWhenFromXML() {
		CreateFilterSetUsingObjects task = new CreateFilterSetUsingObjects();
		Project project = new Project();
		task.init();
		task.setProject(project);
		task.setFiltersetid("testset");
		task.setGenesisxml("etc/testdata/genesis.xml");
		
		GenesisObjectReference ref = new GenesisObjectReference();
		ref.setType("Enterprise/Datacentre/Rack/Node");
		ref.setName("MyEnterprise-00/MyDatacentre-00/MyRack-00/MyNode-00");
		task.addConfiguredObject(ref);
		
		task.execute();
		
		FilterSet filters = (FilterSet) project.getReference("testset");
		assertEquals("MyEnterprise-00 MyDatacentre-00 MyRack-00 MyNode-00", filters.replaceTokens("@Enterprise.Name@ @Datacentre.Name@ @Rack.Name@ @Node.Name@"));
	}
}
