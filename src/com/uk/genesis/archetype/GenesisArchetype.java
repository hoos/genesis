package com.uk.genesis.archetype;

import java.io.File;

/**
 * Provides access to an action that has been defined in the genesis configuration.
 * 
 * @author paul.jones
 */
public interface GenesisArchetype {
	/**
	 * Retrieves the name of the action.
	 * @return the name of the action.
	 */
	String getName();
	
	/**
	 * The file that contains the content of the action.
	 * @return the action content file.
	 */
	File getBuildFile();
	
	/**
	 * Retrieves the requirements of this action.
	 * @return the requirements.
	 */
	GenesisArchetypeRequirement[] getRequirements();
	
	/**
	 * Retrieves the operations associated with this archetype.
	 * @return the operations.
	 */
	GenesisArchetypeOperation[] getOperations();
}
