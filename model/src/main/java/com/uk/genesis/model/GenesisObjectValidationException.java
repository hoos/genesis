package com.uk.genesis.model;

/**
 * Exception used to indicate that a GenesisObject has failed validation.
 * 
 * @author paul.jones
 */
public class GenesisObjectValidationException extends Exception {
	public GenesisObjectValidationException(String message) {
		super(message);
	}
	
	public GenesisObjectValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
