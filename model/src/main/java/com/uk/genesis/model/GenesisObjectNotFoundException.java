package com.uk.genesis.model;

/**
 * Exception thrown when a GenesisObject that doesn't exist is requested.
 * 
 * @author paul.jones
 */
public class GenesisObjectNotFoundException extends Exception {

	public GenesisObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenesisObjectNotFoundException(String message) {
		super(message);
	}
}
