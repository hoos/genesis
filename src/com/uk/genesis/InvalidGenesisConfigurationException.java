package com.uk.genesis;

/**
 * Exception indicating an invalid configuration.
 * @author Hussein Badakhchani
 *
 */
public class InvalidGenesisConfigurationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1574738876869395761L;

	public InvalidGenesisConfigurationException(String message) {
		super(message);
	}

	public InvalidGenesisConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
