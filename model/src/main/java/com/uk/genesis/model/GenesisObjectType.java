package com.uk.genesis.model;

/**
 * Interface to a GenesisObject definition.
 * @author paul.jones
 *
 */
public interface GenesisObjectType {
	/**
	 * Retrieves the parent of this object.
	 * @return the object parent, or null if this object has no parent.
	 */
	GenesisObjectType getParent();
	
	/**
	 * Retrieves the children of this object.
	 * @return the object's children.
	 */
	GenesisObjectType[] getChildren()
			throws ModelException;
	
	/**
	 * Retrieves the name of this genesis object type.
	 * @return the object name.
	 */
	String getName();
	
	/**
	 * Retrieves the qualified name of this genesis object type. For example,
	 * /parent/child. 
	 * @return the qualified name.
	 */
	String getQualifiedName();
	
	/**
	 * Retrieves an instance of the defined object, based on the given
	 * pathing information. The pathing information should be in the form
	 * of /&lt;parent&gt;/&lt;child&gt;
	 * @param path the pathing for the object.
	 * @return the object instance.
	 */
	GenesisObject getInstance(String path)
			throws GenesisObjectNotFoundException;
	
	/**
	 * Retrieves all child instances of this type rooted underneath the given parent.
	 * Note that the provided parent must be either null (if this is a root object), or
	 * of a direct parent type.
	 * @param parent null if this is a root type, or an instance of a direct parent type.
	 * @return all child instances.
	 * @throws ModelException if the provided parent isn't valid.
	 */
	GenesisObject[] getAllChildInstances(GenesisObject parent)
			throws ModelException;
}
