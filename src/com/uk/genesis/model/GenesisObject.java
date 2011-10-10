package com.uk.genesis.model;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public interface GenesisObject {
    /**
     * Retrieves the definition for the object.
     * @return the object type.
     */
    GenesisObjectType getType();

    /**
     * Retrieves the name of the GenesisObject.
     * @return the object name.
     */
    String getName();

    /**
     * Retrieves the qualified name of the GenesisObject.
     * @return the qualified object name.
     */
    String getQualifiedName();

    /**
     * Retrieves a file containing the content of this object.
     * Note that this may return null if there is no file connected
     * to the given schema object, or if the model is stored in a
     * non-file based system (such as a CMDB).
     * @return the content file, or null if none is available
     */
    File getContentLocation();

    /**
     * Retrieves the content connected to this object as
     * a series of name value pairs.
     * @return the content represented as properties.
     */
    Properties getContentAsProperties()
            throws IOException;

    /**
     * Retrieves the parent of this genesis object.
     * @return the object parent.
     */
    GenesisObject getParent();

    /**
     * Requests that the object provide a list of all of its direct children.
     * @return the list of children.
     */
    GenesisObject[] getChildren()
            throws ModelException;

    /**
     * Requests that the object provide a list of
     * all of its children of the given type.
     * @return the list of children.
     * @throws IllegalArgumentException if the given
     * type is not a direct child of this object's type.
     */
    GenesisObject[] getChildren(GenesisObjectType childType)
            throws ModelException;

    /**
     * Requests that the object perform any self validation
     * that it can to ensure the integrity of its configuration.
     * @throws GenesisObjectValidationException
     * if the object does not validate successfully.
     */
    void validate()
            throws GenesisObjectValidationException;
}
