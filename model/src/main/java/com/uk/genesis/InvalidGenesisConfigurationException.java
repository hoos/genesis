package com.uk.genesis;

public class InvalidGenesisConfigurationException extends Exception {
	public InvalidGenesisConfigurationException(String message) {
		super(message);
	}

	public InvalidGenesisConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
