package com.uk.genesis.model;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Interface providing programatic access to a model defined in
 * the primary genesis configuration file.
 * 
 * @author paul.jones
 */
public interface ModelReader {
	/**
	 * Retrieves the root objects for the model.
	 * @return the root objects.
	 */
	GenesisObjectType[] getRootObjectTypes()
			throws ModelException;
	
	/**
	 * Requests a single GenesisObjectType, using xpath style syntax. For example,
	 * finding an object Cabinet that has a parent tree of Datacentre, then Enterprise,
	 * the expression /Enterprise/Datacentre/Cabinet would be used.
	 * @param path the query path for the object.
	 * @return the located object, or null.
	 */
	GenesisObjectType findSingleObjectType(String path)
			throws ModelException;
	
	/**
	 * Retrieves the location of any global configuration data for the genesis instance. Note
	 * that this may null if there is no global configuration configured, or if the object model
	 * actually exists in a CMDB (that doesn't use file based storage).
	 * @return the location of the global content, or null if it isn't available.
	 */
	File getGlobalContentLocation()
			throws ModelException;
	
	/**
	 * Retrieves the global content as a series of name value pairs.
	 * @return the content represented as properties.
	 */
	Properties getGlobalContentAsProperties()
			throws ModelException, IOException;
}
