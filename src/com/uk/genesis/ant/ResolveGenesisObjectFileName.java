package com.uk.genesis.ant;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;

import org.apache.tools.ant.BuildException;

/**
 * Ant task for resolving a given object into a path for the file.
 * 
 * @author paul.jones
 */
public class ResolveGenesisObjectFileName extends BaseGenesisTask {

    private String objectType;
    private String objectName;
    private String propertyName;

    public void setType(String objectType) {
        this.objectType = objectType;
    }

    public void setName(String objectName) {
        this.objectName = objectName;
    }

    public void setProperty(String propertyName) {
        this.propertyName = propertyName;
    }

    public void execute() throws BuildException {
        validate();

        try {
            // Try to find the object
            GenesisObjectType type = getGenesisLoader().getModelReader().findSingleObjectType(this.objectType);
            GenesisObject obj = type.getInstance(this.objectName);

            // Output the property
            getProject().setProperty(this.propertyName, obj.getContentLocation().getAbsolutePath());
        } catch (ModelException ex) {
            throw new BuildException("Genesis Object Type " + this.objectType + " is not valid", ex, getLocation());
        } catch (GenesisObjectNotFoundException ex) {
            throw new BuildException("Genesis Object " + this.objectName + " was not found", ex, getLocation());
        }
    }

    protected void validate() throws BuildException {
        super.validate();

        if (this.objectType == null) {
            throw new BuildException("property type is required", getLocation());
        }
        if (this.objectName == null) {
            throw new BuildException("property name is required", getLocation());
        }
        if (this.propertyName == null) {
            throw new BuildException("property property is required", getLocation());
        }
    }
}
