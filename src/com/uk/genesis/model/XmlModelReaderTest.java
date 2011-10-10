package com.uk.genesis.model;

import java.io.File;
import java.util.Properties;

import com.uk.genesis.GenesisLoader;
import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelReader;
import junit.framework.TestCase;

/**
 * Test cases for the XmlModelReader.
 *
 * @author paul.jones
 */
public class XmlModelReaderTest extends TestCase {
    public void testReaderHasRootObjects() throws Exception {
        ModelReader reader = getReader();

        assertEquals(1, reader.getRootObjectTypes().length);
    }

    public void testReaderLoadsGlobalConfig() throws Exception {
        ModelReader reader = getReader();

        assertEquals(new File("etc/testdata/config/common.xml").getAbsolutePath(),
                reader.getGlobalContentLocation().getAbsolutePath());
    }

    public void testReaderLoadsGlobalContentProperties() throws Exception {
        ModelReader reader = getReader();
        Properties props = reader.getGlobalContentAsProperties();
        //props.list(System.out);
        assertEquals("CommonProperty-00", props.getProperty("Common.Name"));
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

    public void testFoundObjectTypeInHierarchyHasParentOfCorrectType() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");

        assertTrue(rack.getParent() instanceof XmlModelReader.XmlModelGenesisObjectType);
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
        GenesisObject myEnterprise = enterprise.getInstance("/MyEnterprise-00");

        assertNotNull(myEnterprise);
        assertEquals("MyEnterprise-00", myEnterprise.getName());
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

    public void testFoundObjectInstanceHasContext() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

        assertNotNull(myrack.getParent());
        assertEquals("/MyEnterprise-00/MyDatacentre-00", myrack.getParent().getQualifiedName());
    }

    public void testFoundObjectInstanceReturnsParentOfCorrectType() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

        assertTrue(myrack.getParent() instanceof XmlModelReader.XmlModelGenesisObject);
    }

    public void testFoundObjectInstanceHasFileAssociated() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

        assertEquals(
                new File("etc/testdata/config/MyEnterprise-00/physical/MyDatacentre-00/MyRack-00/rack.xml").getAbsoluteFile(),
                myrack.getContentLocation());
    }

    public void testFoundObjectInstanceHasContentAvailable() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");
        Properties props = myrack.getContentAsProperties();

        assertEquals("MyRack-00", props.getProperty("Rack.Name"));
        assertEquals("10", props.getProperty("Rack.Row.rowA.Height"));
        assertEquals("20", props.getProperty("Rack.Row.rowB.Height"));
        assertNull(props.getProperty("Rack"));
    }

    public void testFoundObjectInstanceValidatesForGoodObject() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-00");

        myrack.validate();
    }

    public void testFoundObjectInstanceFailsValidationForBadObject() throws Exception {
        ModelReader reader = getReader();
        GenesisObjectType rack = reader.findSingleObjectType("/Enterprise/Datacentre/Rack");
        GenesisObject myrack = rack.getInstance("/MyEnterprise-00/MyDatacentre-00/MyRack-01");

        try {
            myrack.validate();
            fail("Should have thrown GenesisObjectValidationException");
        } catch (GenesisObjectValidationException ex) {

        }
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
            rack.getInstance("/MyDatacentre/MyRack-00");
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
        loader.load("etc/testdata/genesis-xml-structured.xml");
        return loader.getModelReader();
    }
}
