package com.uk.genesis.ant;

import java.util.ArrayList;
import java.util.List;

import com.uk.genesis.archetype.ArchetypeFactory;
import com.uk.genesis.archetype.GenesisArchetype;
import com.uk.genesis.archetype.GenesisArchetypeOperation;
import com.uk.genesis.archetype.GenesisArchetypeRequirement;
import com.uk.genesis.model.EnumUtils;
import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;

/**
 * Ant task that will automatically define ant tasks for actions that have been defined in the
 * genesis.xml file.
 * 
 * @author paul.jones
 */
public class DefineTargetsForGenesisArchetypes
        extends BaseGenesisTask {

    private List<GenesisObjectReference> objects;
    private List<GenesisObjectReference> availableObjects;
    private String targetsDependOn;

    public DefineTargetsForGenesisArchetypes() {
        objects = new ArrayList<GenesisObjectReference>();
        availableObjects = new ArrayList<GenesisObjectReference>();
    }

    public void addConfiguredObject(GenesisObjectReference objRef) {
        if (objRef.getName() == null) {
            throw new BuildException("property name is required on declared objects", getLocation());
        }
        if (objRef.getType() == null) {
            throw new BuildException("property type is required on declared objects", getLocation());
        }

        objects.add(objRef);
    }

    public void setTargetsdependon(String depends) {
        targetsDependOn = depends;
    }

    public void execute() {
        validate();

        // Determine which of the objects we were passed are actually valid
        for (GenesisObjectReference objRef : objects) {
            if (objRef.isValid(getProject())) {
                availableObjects.add(objRef);
            }
        }

        // Retrieve the factory, and works through the actions
        try {
            ArchetypeFactory factory = getGenesisLoader().getArchetypeFactory();
            for (GenesisArchetype archetype : factory.getArchetypes()) {
                for (GenesisArchetypeOperation operation : archetype.getOperations()) {
                    defineTargetsForAction(archetype, operation);
                }
            }
        } catch (ModelException ex) {
            log("Failed to generate targets for actions: " + ex.getMessage(), ex, Project.MSG_ERR);
        } catch (GenesisObjectNotFoundException ex) {
            log("Failed to generate targets for actions: " + ex.getMessage(), ex, Project.MSG_ERR);
        }
    }

    /**
     * Defines the targets for the provided archetype and operation combination.
     * @param archetype the action.
     */
    private void defineTargetsForAction(GenesisArchetype archetype, GenesisArchetypeOperation operation)
            throws ModelException, GenesisObjectNotFoundException {
        // Process the requirements and find the relevant matching objects
        List<FulfilledGenesisActionRequirement> filledReqs = new ArrayList<FulfilledGenesisActionRequirement>();
        for (GenesisArchetypeRequirement req : archetype.getRequirements()) {
            GenesisObject[] reqObjs = enumerateObjectsForRequirement(req);

            filledReqs.add(new FulfilledGenesisActionRequirement(req, reqObjs));
        }

        // Generate the task
        MultiRequirementTaskExecutor executor =
                new MultiRequirementTaskExecutor(archetype, operation, filledReqs);
        executor.setProject(getProject());
        executor.setTaskName("genesisaction");

        // Define a task that triggers the given action
        Target target = new Target();
        target.setName(archetype.getName() + ":" + operation.getName());
        target.addTask(executor);
        if (targetsDependOn != null) {
            target.setDepends(targetsDependOn);
        }
        executor.setOwningTarget(target);

        // Add the target to the project
        getProject().addTarget(target);
        log("Defined target " + target.getName() + " based on Genesis Action", Project.MSG_VERBOSE);
    }

    private GenesisObjectReference getBestReferenceForRequirement(GenesisArchetypeRequirement req) {
        // Start at the deepest type, then work backwards till we find a match
        GenesisObjectType curType = req.getType();
        while (curType != null) {
            for (GenesisObjectReference ref : availableObjects) {
                if (ref.getType().equals(curType.getQualifiedName())) {
                    // We've found a declared object that matches the required type
                    return ref;
                }
            }

            curType = curType.getParent();
        }

        // We didn't find any reference that matched at any depth
        return null;
    }

    private GenesisObject[] enumerateObjectsForRequirement(GenesisArchetypeRequirement req)
            throws ModelException, GenesisObjectNotFoundException {
        GenesisObjectReference bestReference = getBestReferenceForRequirement(req);
        ModelReader reader = getGenesisLoader().getModelReader();

        // Generate a list of all objects that match the given requirement, based on the best reference

        if (bestReference == null) {
            // We need to enumerate root objects
            GenesisObjectType[] rootTypes = reader.getRootObjectTypes();

            return EnumUtils.enumerateChildObjectsOfType(null, rootTypes, req.getType());
        } else if (bestReference.getType().equals(req.getType().getQualifiedName())) {
            GenesisObject bestRefObj = req.getType().getInstance(bestReference.getName());

            return new GenesisObject[]{bestRefObj};
        } else {
            GenesisObjectType bestRefType = reader.findSingleObjectType(bestReference.getType());
            GenesisObject bestRefObj = bestRefType.getInstance(bestReference.getName());

            // Base at the best reference, and work from there
            return EnumUtils.enumerateChildObjectsOfType(bestRefObj, req.getType());
        }
    }

    public class MultiRequirementTaskExecutor extends Task {

        private final GenesisArchetype archetype;
        private final GenesisArchetypeOperation operation;
        private final List<FulfilledGenesisActionRequirement> reqs;
        private GenesisObject[] axisInstances;

        public MultiRequirementTaskExecutor(GenesisArchetype action, GenesisArchetypeOperation operation,
                List<FulfilledGenesisActionRequirement> reqs) {
            this.archetype = action;
            this.operation = operation;
            this.reqs = reqs;
            this.axisInstances = new GenesisObject[reqs.size()];
        }

        public void execute() {
            // Run the matrix of requirement instances against each other
            executeRequirementAxis(0);
        }

        private void executeRequirementAxis(int pos) {
            // If the position exists, enumerate through each
            if (reqs.size() > pos) {
                FulfilledGenesisActionRequirement req = reqs.get(pos);

                for (GenesisObject obj : req.getObjects()) {
                    // Store the object currently being used on this axis
                    axisInstances[pos] = obj;

                    // Enumerate the next axis
                    executeRequirementAxis(pos + 1);
                }
            } else {
                // Log header
                log("About to execute action " + archetype.getName() + " with: ", Project.MSG_INFO);


                // We're at the deepest level. We just need to build parameters and execute the call.
                Ant actionCall = new Ant();

                // Configure the call
                actionCall.setProject(getProject());
                actionCall.setAntfile(archetype.getBuildFile().getName());
                actionCall.setDir(archetype.getBuildFile().getParentFile());
                actionCall.setTarget(operation.getTarget());
                actionCall.setOwningTarget(getOwningTarget());

                // Build the properties
                for (int i = 0; i < reqs.size(); ++i) {

                    GenesisArchetypeRequirement req = reqs.get(i).getRequirement();

                    if (req.getQNameProperty() != null) {
                        Property prop = actionCall.createProperty();
                        prop.setName(req.getQNameProperty());
                        prop.setValue(axisInstances[i].getQualifiedName());
                    }
                    if (req.getNameProperty() != null) {
                        Property prop = actionCall.createProperty();
                        prop.setName(req.getNameProperty());
                        prop.setValue(axisInstances[i].getName());
                    }

                    // Log detail
                    log("  " + axisInstances[i].getQualifiedName() + " (" + axisInstances[i].getType().getQualifiedName() + ")", Project.MSG_INFO);
                }

                // Do it.
                actionCall.execute();
            }
        }
    }
}
