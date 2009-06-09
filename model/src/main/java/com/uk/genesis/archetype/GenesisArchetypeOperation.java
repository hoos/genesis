package com.uk.genesis.archetype;

/**
 * Data class providing details of an operation that can occur on an
 * archetype.
 * 
 * @author paul.jones
 */
public class GenesisArchetypeOperation {
	/**
	 * The name of the operation.
	 */
	private final String name;
	
	/**
	 * The target to invoke with the operation.
	 */
	private final String target;

	public GenesisArchetypeOperation(final String name, final String target) {
		super();
		this.name = name;
		this.target = target;
	}

	/**
	 * Retrieves the name of the operation.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves the target of the operation.
	 * @return the target.
	 */
	public String getTarget() {
		return target;
	}
}
