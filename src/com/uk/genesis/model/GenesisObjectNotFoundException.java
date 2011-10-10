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

    public GenesisObjectNotFoundException(final String message,
                                            final Throwable cause) {
        super(message, cause);
    }

    public GenesisObjectNotFoundException(final String message) {
        super(message);
    }
}
