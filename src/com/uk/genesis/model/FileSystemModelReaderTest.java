package com.uk.genesis.model;

import java.io.File;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;

import com.uk.genesis.GenesisLoader;
import junit.framework.TestCase;

final public class FileSystemModelReaderTest extends TestCase {
	public void testReaderHasRootObjects() throws Exception {
		final ModelReader reader = getReader();

		assertEquals(1, reader.getRootObjectTypes().length);
	}

	public void testRequestingRootObjectInstancesReturnsValidObjects() throws Exception {
		final ModelReader reader = getReader();

		final GenesisObjectType enterprise = reader.findSingleObjectType("/Enterprise");
		final GenesisObject[] enterprises = enterprise.getAllChildInstances(null);

		assertEquals(1, enterprises.length);
		assertEquals("/MyEnterprise-00", enterprises[0].getQualifiedName());
	}

	public void testRequestingRootObjectInstancesWithNonNullParentThrowsException()
			throws Exception {
		final ModelReader reader = getReader();

		final GenesisObjectType enterprise = reader.findSingleObjectType("/Enterprise");
		final GenesisObject MyEnterprise = enterprise.getInstance("/MyEnterprise-00");

		try {
			enterprise.getAllChildInstances(MyEnterprise);
			fail("Should have thrown ModelException");
		} catch (ModelException ex) {
		}
	}

	public void testRequestingObjectInstancesWithInvalidParentThrowsException()
			throws Exception {
		final ModelReader reader = getReader();

		final GenesisObjectType datacentre = reader.findSingleObjectType("/Enterprise/Datacentre");
		final GenesisObject mydatacentre = datacentre.getInstance("/MyEnterprise-00/MyDatacentre-00");

		try {
			datacentre.getAllChildInstances(mydatacentre);
			fail("Should have thrown ModelException");
		} catch (ModelException ex) {
		}
		}

	public void testReaderLoadsGlobalConfig() throws Exception {
		final ModelReader reader = getReader();

		assertEquals(new File("etc/testdata/config/common.xml").getAbsolutePath(),
				reader.getGlobalContentLocation().getAbsolutePath());
	}

	public void testReaderLoadsGlobalContentProperties() throws Exception {
            /*
             * Hoos: This test was designed for standard properties files.
             * They do not work well with XML files, they should be removed if
             * it cause too much maintenance.
             */

            final ModelReader reader = getReader();
            final Properties props = reader.getGlobalContentAsProperties();
            //props.list(System.out);
            assertNotNull(props.getProperty("<Common>"));
            assertNotNull(props.getProperty("<Name>CommonProperty-00</Name>"));
	}

	public void testObjectHasCorrectName() throws Exception {
		ModelReader reader = getReader();

		assertEquals("Enterprise", reader.getRootObjectTypes()[0].getName());
	}

	public void testRootObjectHasNoParents() throws Exception {
		ModelReader reader = getReader();

		assertNull(reader.getRootObjectTypes()[0].getParent());
	}

	public void testObjectHasChildren() throws Exception {
		ModelReader reader = getReader();

		assertEquals(2, reader.getRootObjectTypes()[0].getChildren().length);
	}

	public void testRootObjectHasValidQualifiedName() throws Exception {
		ModelReader reader = getReader();

		assertEquals("/Enterprise", reader.getRootObjectTypes()[0].getQualifiedName());
	}

	public void testChildrenHaveParent() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType root = reader.getRootObjectTypes()[0];

		assertEquals(root, root.getChildren()[0].getParent());
	}

	public void testFindObjectTypeInHierarchy() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");

		assertNotNull(rack);
		assertEquals("Rack", rack.getName());
	}

	public void testObjectTypeNotInHierarchyReturnsNull() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType missingrack = reader.findSingleObjectType("/Enterprise/Datacentre/MissingRack");

		assertNull(missingrack);
	}

	public void testFoundObjectTypeInHierarchyHasContext() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");

		GenesisObjectType datacentre = rack.getParent();
		assertNotNull(datacentre);
		assertEquals("Datacentre", datacentre.getName());

		GenesisObjectType enterprise = datacentre.getParent();
		assertNotNull(enterprise);
		assertEquals("Enterprise", enterprise.getName());

		assertNull(enterprise.getParent());
	}

	public void testFoundObjectTypeInHierarchyHasValidQualifiedName() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");

		assertEquals("/Enterprise/Datacentre/Rack", rack.getQualifiedName());
	}

	public void testFoundRootObjectTypeInHierarchyHasContext() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType enterprise = reader.findSingleObjectType("/Enterprise");

		assertNull(enterprise.getParent());
	}

	public void testFindObjectInstanceForRoot() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType enterprise = reader.findSingleObjectType("/Enterprise");
		GenesisObject MyEnterprise = enterprise.getInstance("/MyEnterprise-00");

		assertNotNull(MyEnterprise);
		assertEquals("MyEnterprise-00", MyEnterprise.getName());
	}

	public void testFindObjectInstance() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

		assertNotNull(myrack);
		assertEquals("MyRack-00", myrack.getName());
	}

	public void testFoundObjectInstanceHasType() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

		assertEquals(rack, myrack.getType());
	}

	public void testFoundObjectInstanceHasQualifiedName() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

		assertEquals("/MyEnterprise-00/MyDatacentre-00/MyRack-00", myrack.getQualifiedName());
	}

	public void testFoundObjectInstanceHasFileAssociated() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

		assertEquals(
				new File("etc/testdata/config/MyEnterprise-00/physical/MyDatacentre-00/MyRack-00/rack.xml").getAbsoluteFile(),
				myrack.getContentLocation());
	}

	public void testFoundObjectInstanceHasPropertiesAvailable() throws Exception {
                /*
                 * Hoos: This test was designed for standard properties files.
                 * They do not work well with XML files, they should be removed if
                 * it cause too much maintenance.
                 */
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

		Properties props = myrack.getContentAsProperties();
                //props.list(System.out);
		assertEquals("name=\"rowB\"> ", props.getProperty("<Row"));
		assertNotNull("name=\"rowB\"> ", props.getProperty("<Rack>"));
	}

	public void testFoundObjectInstanceCanListChildren() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType datacentre = reader.findSingleObjectType("/Enterprise/Datacentre");
		GenesisObject mydatacentre = datacentre.getInstance("/MyEnterprise-00/MyDatacentre-00");

		GenesisObject[] children = mydatacentre.getChildren();
		assertEquals(2, children.length);
		assertEquals(
				new File("etc/testdata/config/MyEnterprise-00/physical/MyDatacentre-00/MyRack-00/rack.xml").getAbsoluteFile(),
				children[0].getContentLocation());
		assertEquals(
				new File("etc/testdata/config/MyEnterprise-00/physical/MyDatacentre-00/MyRack-01/rack.xml").getAbsoluteFile(),
				children[1].getContentLocation());
	}

	public void testObjectNotExistingThrowsObjectNotFoundException() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType missingrack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		try {
			missingrack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-03");
			fail("Should have thrown GenesisObjectNotFoundException");
		} catch (GenesisObjectNotFoundException ex) {
		}
	}

	public void testFindObjectInstanceWithTooShortPathThrowsIllegalArgumentException() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		try {
			rack.getInstance("/MyDatacentre-00/MyRack-00");
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
		}
	}

	public void testFindObjectInstanceWithTooLongPathThrowsIllegalArgumentException() throws Exception {
		ModelReader reader = getReader();
		GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
		try {
			rack.getInstance("/MyEnterprise-00/MyRandom-00/MyDatacentre-00/MyRack-00");
			fail("Should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
		}
	}

	private ModelReader getReader() throws Exception {
		GenesisLoader loader = new GenesisLoader();
		loader.load("etc/testdata/genesis-structured.xml");
		return loader.getModelReader();
	}
}
