package com.uk.genesis.model;

/**
 * Secondary interface to be implemented by ModelReader instances
 * that allows the reader to be configured. Configuration information
 * includes details such as the XML element containing model data and
 * genesis directory root.
 *
 * @author paul.jones
 */
public interface ConfigurableModelReader extends ModelReader {
    /**
     * Indicates that the model reader should configure itself using
     * data in the given environment.
     * @param env the environment
     */
    void configure(GenesisEnvironment env);
}
