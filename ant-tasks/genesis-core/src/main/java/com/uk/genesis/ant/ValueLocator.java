package com.uk.genesis.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Task that provides the ability to query a given configuration value across
 * all object types in the hierarchy.
 * 
 * @author paul.jones
 */
public class ValueLocator extends BaseGenesisTask {

    private String type;
    private String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void execute() {
        validate();

        // Retrieve the model reader, and resolve the type
        ModelReader mr = getGenesisLoader().getModelReader();
        try {
            GenesisObjectType objType = mr.findSingleObjectType(getType());
            if (objType == null) {
                throw new BuildException("The type " + getType() + " is invalid", getLocation());
            }

            // Find all instances of the type
            GenesisObject[] allInstances = getAllInstances(objType);

            // Enumerate all values of the variable
            for (GenesisObject instance : allInstances) {
                Properties props = instance.getContentAsProperties();

                if (props.containsKey(getProperty())) {
                    log(instance.getQualifiedName() + ": " + props.getProperty(getProperty()));
                }
            }
        } catch (ModelException ex) {
            throw new BuildException(ex.getMessage(), ex, getLocation());
        } catch (IOException ex) {
            throw new BuildException(ex.getMessage(), ex, getLocation());
        }
    }

    @Override
    protected void validate() {
        super.validate();

        if (getType() == null) {
            throw new BuildException("type must be set", getLocation());
        }
        if (getProperty() == null) {
            throw new BuildException("property must be set", getLocation());
        }
    }

    protected GenesisObject[] getAllInstances(GenesisObjectType objType)
            throws ModelException {
        // If the type is a root object, then just get its instances
        GenesisObjectType parentType = objType.getParent();
        if (parentType == null) {
            return objType.getAllChildInstances(null);
        }

        // Get all parent instances
        GenesisObject[] parents = getAllInstances(parentType);
        List<GenesisObject> result = new ArrayList<GenesisObject>();
        for (GenesisObject parent : parents) {
            result.addAll(Arrays.asList(objType.getAllChildInstances(parent)));
        }

        return result.toArray(new GenesisObject[result.size()]);
    }
}
