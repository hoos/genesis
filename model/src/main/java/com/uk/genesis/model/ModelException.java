package com.uk.genesis.model;

/**
 * Exception indicating that a problem has occurred interpreting
 * the model.
 * 
 * @author paul.jones
 */
public class ModelException extends Exception {
	public ModelException(String message) {
		super(message);
	}

	public ModelException(String message, Throwable cause) {
		super(message, cause);
	}
}
