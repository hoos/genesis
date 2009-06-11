package com.uk.genesis.model;

/**
 * Exception thrown when a GenesisObject that doesn't exist is requested.
 * 
 * @author paul.jones
 * @author hussein.badakhchani
 */
public class GenesisObjectNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6531356846975616579L;

	public GenesisObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenesisObjectNotFoundException(String message) {
		super(message);
	}
}
