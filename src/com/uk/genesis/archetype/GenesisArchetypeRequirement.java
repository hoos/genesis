package com.uk.genesis.archetype;

import com.uk.genesis.model.GenesisObjectType;

/**
 * Defines a requirement of an archetype.
 * @author paul.jones
 */
public class GenesisArchetypeRequirement {
	/**
	 * The type that the action requires.
	 */
	private GenesisObjectType type;
	
	/**
	 * The property to expose the qualified object name under.
	 */
	private String qnameProperty;
	
	/**
	 * The property to expose the simple object name under.
	 */
	private String nameProperty;
	
	/**
	 * Creates a new requirement for the given type and exposed property.
	 * @param type the type that  the action requires.
	 * @param qNameProperty the property that the qualified object name will be exposed under.
	 * @param nameProperty the property that the local object name will be exposed under.
	 */
	public GenesisArchetypeRequirement(GenesisObjectType type, String qnameProperty, String nameProperty) {
		this.type = type;
		this.qnameProperty = qnameProperty;
		this.nameProperty = nameProperty;
	}

	/**
	 * Retrieves the type that the action requires.
	 * @return the required type.
	 */
  public GenesisObjectType getType() {
    return type;
  }
	
  /**
   * Retrieves the name of the property that the qualified object instance will be exposed under.
   * @return the name of the property.
   */
  public String getQNameProperty() {
    return qnameProperty;
  }

  /**
   * Retrieves the name of the property that the local object name will be exposed under.
   * @return the name of the property.
   */
  public String getNameProperty() {
    return nameProperty;
  }
}
