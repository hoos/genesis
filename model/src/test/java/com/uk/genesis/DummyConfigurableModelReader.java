package com.uk.genesis;

import java.io.File;
import java.util.Properties;

import com.uk.genesis.model.ConfigurableModelReader;
import com.uk.genesis.model.GenesisEnvironment;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

public class DummyConfigurableModelReader 
		implements ConfigurableModelReader, ModelReader {
	private GenesisEnvironment environment;

	public void configure(GenesisEnvironment env) {
		this.environment = env;
	}

	public GenesisObjectType findSingleObjectType(String path) throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public GenesisObjectType[] getRootObjectTypes() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public File getGlobalContentLocation() {
		return null;
	}
	
	public Properties getGlobalContentAsProperties() {
		return new Properties();
	}
	
	public GenesisEnvironment getEnvironment() {
		return environment;
	}
}
