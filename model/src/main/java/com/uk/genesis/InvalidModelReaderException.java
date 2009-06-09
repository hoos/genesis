package com.uk.genesis;

/**
 * Exception indicating that a configured ModelReader is invalid.
 * This may be because the class doesn't exist, or is not actually
 * a model reader.
 * 
 * @author paul.jones
 */
public class InvalidModelReaderException 
		extends InvalidGenesisConfigurationException {
	public InvalidModelReaderException(String message) {
		super (message);
	}
	
	public InvalidModelReaderException(String message, Exception ex) {
		super (message, ex);
	}
}
