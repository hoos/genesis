package com.uk.genesis.ant;

import java.io.IOException;
import java.util.Properties;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.apache.tools.ant.BuildException;

public class RetrieveObjectProperty extends BaseGenesisTask {

    private String type;
    private String name;
    private String token;
    private String property;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void execute() {
        validate();

        try {
            ModelReader modelReader = getGenesisLoader().getModelReader();
            GenesisObjectType type = modelReader.findSingleObjectType(getType());
            if (type == null) {
                throw new BuildException("Type " + getType() + " is invalid");
            }
            GenesisObject object = type.getInstance(getName());
            Properties props = object.getContentAsProperties();
            getProject().setProperty(getProperty(), props.getProperty(getToken()));
        } catch (ModelException ex) {
            throw new BuildException(ex.getMessage(), ex, getLocation());
        } catch (GenesisObjectNotFoundException ex) {
            throw new BuildException("Object " + getName() + " of type " + getType() + " was not found",
                    ex, getLocation());
        } catch (IOException ex) {
            throw new BuildException(ex.getMessage(), ex, getLocation());
        }
    }

    @Override
    protected void validate() {
        super.validate();

        if (getName() == null) {
            throw new BuildException("name parameter must be set", getLocation());
        }
        if (getType() == null) {
            throw new BuildException("type parameter must be set", getLocation());
        }
        if (getProperty() == null) {
            throw new BuildException("property parameter must be set", getLocation());
        }
        if (getToken() == null) {
            throw new BuildException("token parameter must be set", getLocation());
        }
    }
}
