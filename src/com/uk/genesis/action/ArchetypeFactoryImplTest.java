package com.uk.genesis.action;

import java.io.File;

import com.uk.genesis.GenesisLoader;
import com.uk.genesis.archetype.ArchetypeFactory;
import com.uk.genesis.archetype.GenesisArchetype;
import com.uk.genesis.model.GenesisObjectType;
import junit.framework.TestCase;

/**
 * Test cases for the action factory implementation.
 *
 * @author paul.jones
 */
public class ArchetypeFactoryImplTest extends TestCase {

	public void testFactoryReturnsCorrectItemCount() throws Exception {
		assertEquals(2, getFactory("genesis-structured-with-actions.xml").getArchetypes().length);
	}

	public void testFactoryItemHasName() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[0];

		assertEquals("MyApplication-00", action.getName());
	}

	public void testFactoryItemHasOperations() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[0];

		assertEquals(2, action.getOperations().length);
		assertEquals("build", action.getOperations()[0].getName());
		assertEquals("do-build-MyApplication-00", action.getOperations()[0].getTarget());
		assertEquals("package", action.getOperations()[1].getName());
		assertEquals("do-package-MyApplication-00", action.getOperations()[1].getTarget());
	}

	public void testFactoryLoadsUniqueItems() throws Exception {
		GenesisArchetype[] actions = getFactory("genesis-structured-with-actions.xml").getArchetypes();

		assertEquals("MyApplication-00", actions[0].getName());
		assertEquals("MyApplication-01", actions[1].getName());
	}

	public void testFactoryItemHasBuildFile() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[0];

		assertEquals(new File("etc/testdata/archetypes/MyEnterprise-00/MyApplication-00/build-MyApplication-00.xml").getAbsoluteFile(),
				action.getBuildFile());
	}

	public void testFactoryHandlesItemWithNoRequirements() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[0];

		assertEquals(0, action.getRequirements().length);
	}

	public void testFactoryHandlesItemWithRequirements() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[1];

		assertEquals(2, action.getRequirements().length);
	}

	public void testFactoryItemRequirementHasProperties() throws Exception {
		GenesisArchetype action = getFactory("genesis-structured-with-actions.xml").getArchetypes()[1];

		assertEquals("nodeid", action.getRequirements()[0].getQNameProperty());
		assertEquals("nodename", action.getRequirements()[0].getNameProperty());
	}

	public void testFactoryItemRequirementHasType() throws Exception {
		GenesisLoader loader = getLoader("genesis-structured-with-actions.xml");
		GenesisArchetype action = loader.getArchetypeFactory().getArchetypes()[1];
		GenesisObjectType type = loader.getModelReader().findSingleObjectType("/Enterprise/Datacentre/Rack/Node");

		assertEquals(type.getQualifiedName(), action.getRequirements()[0].getType().getQualifiedName());
	}

	private GenesisLoader getLoader(String file) throws Exception {
		GenesisLoader loader = new GenesisLoader();
		loader.load("etc/testdata/" + file);

		return loader;
	}

	private ArchetypeFactory getFactory(String file) throws Exception {
		return getLoader(file).getArchetypeFactory();
	}
}
