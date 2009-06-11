package com.uk.genesis;

import java.io.File;
import java.io.FileNotFoundException;

import com.uk.genesis.model.GenesisEnvironment;
import com.uk.genesis.model.XmlModelReader;

import junit.framework.TestCase;

public class GenesisLoaderTest extends TestCase {
	private GenesisLoader mLoader;
	
	public void setUp() {
		mLoader = new GenesisLoader();
	}
	
	public void testLoadHandlesValidFileWithEmptyModel() 
			throws Exception {
		mLoader.load("etc/testdata/genesis-empty.xml");
	}
	
	public void testLoadThrowsFileNotFoundExceptionForEmptyXml() 
			throws Exception {
		try {
			mLoader.load("etc/testdata/genesis-missing.xml");
			fail("Should have thrown FileNotFoundException");
		} catch (FileNotFoundException ex) {
		}
	}
	
	public void testLoadRejectsInvalidModelReader() 
			throws Exception {
		try {
			mLoader.load("etc/testdata/genesis-invalidreader.xml");
			fail("Should have thrown InvalidModelReaderException");
		} catch (InvalidModelReaderException ex) {
		}
	}
	
	public void testLoadCreatesValidModelReaderInstance() 
			throws Exception {
		mLoader.load("etc/testdata/genesis-empty.xml");
		
		assertNotNull(mLoader.getModelReader());
		assertTrue(mLoader.getModelReader() instanceof XmlModelReader);
	}
	
	public void testLoadConfiguresConfigurableModelReaderInstance() 
			throws Exception {
		mLoader.load("etc/testdata/genesis-testreader.xml");
		
		DummyConfigurableModelReader reader = 
			(DummyConfigurableModelReader) mLoader.getModelReader();
		GenesisEnvironment env = reader.getEnvironment();
		
		assertNotNull(env);
		assertEquals(new File("etc/testdata/").getAbsoluteFile(), env.getGenesisRoot());
	}
	
	public void testProvidesActionFactoryForNoActions() throws Exception {
		mLoader.load("etc/testdata/genesis-empty.xml");
		
		assertNotNull(mLoader.getArchetypeFactory());
		assertEquals(0, mLoader.getArchetypeFactory().getArchetypes().length);
	}
	
	public void testProvidesActionFactoryForActions() throws Exception {
		mLoader.load("etc/testdata/genesis-structured-with-actions.xml");
		
		assertNotNull(mLoader.getArchetypeFactory());
		assertEquals(2, mLoader.getArchetypeFactory().getArchetypes().length);
	}
}
