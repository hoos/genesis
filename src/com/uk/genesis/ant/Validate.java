package com.uk.genesis.ant;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.GenesisObjectValidationException;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Validates either the entire object hierarchy, or the hierarchy starting at a given point.
 * 
 * @author paul.jones
 */
public class Validate extends BaseGenesisTask {

    private String objectType;
    private String objectName;
    private boolean validateChildren = true;

    public void setType(String objectType) {
        this.objectType = objectType;
    }

    public void setName(String objectName) {
        this.objectName = objectName;
    }

    public void setValidateChildren(boolean validateChildren) {
        this.validateChildren = validateChildren;
    }

    public void execute() throws BuildException {
        validate();

        // Fetch the model reader
        ModelReader modelReader = getGenesisLoader().getModelReader();

        // Checking whether the validations succeeded
        boolean success = true;

        // If an object was specified, start there
        if (this.objectName != null) {
            try {
                // Try to find the object
                GenesisObjectType type = modelReader.findSingleObjectType(this.objectType);
                GenesisObject obj = type.getInstance(this.objectName);

                success = validateObject(obj);
            } catch (ModelException ex) {
                throw new BuildException("Genesis Object Type " + this.objectType + " is not valid - " + ex.getMessage(), ex, getLocation());
            } catch (GenesisObjectNotFoundException ex) {
                throw new BuildException("Genesis Object " + this.objectName + " was not found - " + ex.getMessage(), ex, getLocation());
            }
        } else {
            try {
                // Walk through each of instance of each root type
                for (GenesisObjectType rootType : modelReader.getRootObjectTypes()) {
                    GenesisObject[] rootInstances = rootType.getAllChildInstances(null);

                    for (GenesisObject rootInstance : rootInstances) {
                        if (!validateObject(rootInstance)) {
                            success = false;
                        }
                    }
                }
            } catch (ModelException ex) {
                throw new BuildException("Could not enumerate root types - " + ex.getMessage(), ex, getLocation());
            }
        }

        if (!success) {
            throw new BuildException("Validation failed", getLocation());
        }
    }

    protected void validate() throws BuildException {
        super.validate();

        // Name and Type must either both be specified, or both not be specified
        if (this.objectType != null || this.objectName != null) {
            if (this.objectType == null) {
                throw new BuildException("property type is required when name property is specified", getLocation());
            }
            if (this.objectName == null) {
                throw new BuildException("property name is required when type property is specified", getLocation());
            }
        }
    }

    protected boolean validateObject(GenesisObject obj) {
        boolean result = true;

        // Validate the primary object
        try {
            obj.validate();

            getProject().log("Object " + obj.getQualifiedName() + " of type " + obj.getType().getQualifiedName() + " successfully validated", Project.MSG_VERBOSE);
        } catch (GenesisObjectValidationException ex) {
            getProject().log("Object " + obj.getQualifiedName() + " of type " + obj.getType().getQualifiedName() +
                    " failed validation - " + ex.getMessage(), Project.MSG_ERR);
            result = false;
        }

        // If we're validating children, do the children too
        if (validateChildren) {
            try {
                for (GenesisObjectType childType : obj.getType().getChildren()) {
                    for (GenesisObject child : obj.getChildren(childType)) {
                        if (!validateObject(child)) {
                            result = false;
                        }
                    }
                }
            } catch (ModelException ex) {
                getProject().log("Failed to get child types for " + obj.getType().getQualifiedName() +
                        " - " + ex.getMessage(), Project.MSG_ERR);
            }
        }

        return result;
    }
}
