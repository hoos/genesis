package com.uk.genesis.archetype;

/**
 * Provides the ability to work with archetypes defined in the genesis.xml file.
 * 
 * @author paul.jones
 */
public interface ArchetypeFactory {
	/**
	 * Retrieves all of the defined archetypes.
	 * @return the actions.
	 */
	GenesisArchetype[] getArchetypes();
}
