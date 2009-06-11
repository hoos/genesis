package com.uk.genesis.ant;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;

/**
 * Ant task that enumerates child objects of the given object.
 * 
 * @author paul.jones
 */
public class ForEachGenesisObject extends BaseGenesisTask {

    private String type;
    private String in;
    private String pathParam;
    private String nameParam;
    private String qualifiednameParam;
    private String typeParam;
    private MacroDef macroDef;

    /**
     * Sets the type of genesis object that is desired.
     * @param type the type of the object that is to be enumerated.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the name of the genesis object that is the parent of the objects to be enumerated.
     * @param name the name of the object that will have its children enumerated.
     */
    public void setIn(String in) {
        this.in = in;
    }

    /**
     * Sets the name of the parameter that will be made available within the macro containing
     * the path to the enumerated file.
     * @param pathParam the name of the parameter to use
     */
    public void setPathParam(String pathParam) {
        this.pathParam = pathParam;
    }

    /**
     * Sets the name of the parameter that will be made available within the macro containing
     * the (local) name of the enumerated object.
     * @param nameParam the name of the parameter to use
     */
    public void setNameParam(String nameParam) {
        this.nameParam = nameParam;
    }

    /**
     * Sets the name of the parameter that will be made available within the macro containing
     * the (qualified) name of the enumerated object.
     * @param nameParam the name of the parameter to use
     */
    public void setQualifiedNameParam(String qualifiedNameParam) {
        this.qualifiednameParam = qualifiedNameParam;
    }

    /* Sets the name of the parameter that will be made available within the macro containing
     * the type of the enumerated object.
     * @param typeParam the name of the parameter to use
     */
    public void setTypeParam(String typeParam) {
        this.typeParam = typeParam;
    }

    /**
     * Creates a sequential object that can be configured. Note that this delegates to a macrodef task,
     * allowing it to assist with parameter substitution.
     * @return the sequential object instance.
     */
    public Object createSequential() {
        macroDef = new MacroDef();
        macroDef.setProject(getProject());

        return macroDef.createSequential();
    }

    /**
     * Executes the task.
     */
    public void execute() {
        validate();

        // Create a macro attribute
        if (macroDef.getAttributes().isEmpty()) {
            if (pathParam != null) {
                MacroDef.Attribute attribute = new MacroDef.Attribute();
                attribute.setName(pathParam);
                macroDef.addConfiguredAttribute(attribute);
            }
            if (nameParam != null) {
                MacroDef.Attribute attribute = new MacroDef.Attribute();
                attribute.setName(nameParam);
                macroDef.addConfiguredAttribute(attribute);
            }
            if (qualifiednameParam != null) {
                MacroDef.Attribute attribute = new MacroDef.Attribute();
                attribute.setName(qualifiednameParam);
                macroDef.addConfiguredAttribute(attribute);
            }
            if (typeParam != null) {
                MacroDef.Attribute attribute = new MacroDef.Attribute();
                attribute.setName(typeParam);
                macroDef.addConfiguredAttribute(attribute);
            }
        }

        // Find the parent object
        GenesisObjectType enumType = null;
        GenesisObject enumObj = null;
        try {
            enumType = getGenesisLoader().getModelReader().findSingleObjectType(type);
            if (enumType == null) {
                throw new BuildException("Invalid enumerable object type " + type, getLocation());
            }

            GenesisObjectType containerType = enumType.getParent();
            enumObj = containerType.getInstance(in);
        } catch (ModelException ex) {
            throw new BuildException("Invalid enumerable object type - " + ex.getMessage(), ex, getLocation());
        } catch (GenesisObjectNotFoundException ex) {
            throw new BuildException("Invalid enumerable object not found - " + ex.getMessage(), ex, getLocation());
        }

        // Request that the parent object enumerate itself
        try {
            for (GenesisObject child : enumObj.getChildren(enumType)) {
                executeSingleIteration(child.getName(), child.getQualifiedName(), child.getType().getQualifiedName(),
                        child.getContentLocation().getAbsolutePath());
            }
        } catch (ModelException ex) {
            throw new BuildException("Failed to enumerate " + enumObj.getName() + " - " + ex.getMessage(), ex, getLocation());
        }
    }

    protected void executeSingleIteration(String name, String qualifiedName, String typeName, String path) {
        // Create the macro-instance
        MacroInstance instance = new MacroInstance();
        instance.setProject(getProject());
        instance.setOwningTarget(getOwningTarget());
        instance.setMacroDef(macroDef);

        if (pathParam != null) {
            instance.setDynamicAttribute(pathParam.toLowerCase(), path);
        }
        if (nameParam != null) {
            instance.setDynamicAttribute(nameParam.toLowerCase(), name);
        }
        if (qualifiednameParam != null) {
            instance.setDynamicAttribute(qualifiednameParam.toLowerCase(), qualifiedName);
        }
        if (typeParam != null) {
            instance.setDynamicAttribute(typeParam.toLowerCase(), typeName);
        }

        instance.execute();
    }

    /**
     * Validates the task configuration.
     */
    protected void validate() {
        super.validate();

        if (macroDef == null) {
            throw new BuildException("An embedded <sequential> element must be supplied", getLocation());
        }
        if (type == null) {
            throw new BuildException("No object type was specified", getLocation());
        }
        if (in == null) {
            throw new BuildException("No object container name was specified", getLocation());
        }
    }
}
