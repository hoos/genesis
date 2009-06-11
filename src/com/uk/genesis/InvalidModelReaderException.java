package com.uk.genesis;

/**
 * Exception indicating that a configured ModelReader is invalid.
 * This may be because the class doesn't exist, or is not actually
 * a model reader.
 * 
 * @author paul.jones
 * @author hussein.badakhchani
 */
public class InvalidModelReaderException 
		extends InvalidGenesisConfigurationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5881566038270151677L;

	public InvalidModelReaderException(String message) {
		super (message);
	}
	
	public InvalidModelReaderException(String message, Exception ex) {
		super (message, ex);
	}
}
