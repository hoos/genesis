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

    public InvalidGenesisConfigurationException(final String message) {
        super(message);
    }

    public InvalidGenesisConfigurationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
