package com.uk.genesis.model;

/**
 * Exception indicating that a problem has occurred
 * interpreting the model.
 *
 * @author paul.jones
 * @author hussein.badakhchani
 */
public class ModelException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 2541035091199413245L;

    public ModelException(final String message) {
        super(message);
    }

    public ModelException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
