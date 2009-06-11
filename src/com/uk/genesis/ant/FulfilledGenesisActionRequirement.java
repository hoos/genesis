package com.uk.genesis.ant;

import com.uk.genesis.archetype.GenesisArchetypeRequirement;
import com.uk.genesis.model.GenesisObject;

/**
 * Carrier class for handling a fulfilled requirement.
 * @author paul.jones
 */
public class FulfilledGenesisActionRequirement {

    private GenesisArchetypeRequirement requirement;
    private GenesisObject[] objects;

    public FulfilledGenesisActionRequirement(GenesisArchetypeRequirement requirement, GenesisObject[] objects) {
        this.requirement = requirement;
        this.objects = objects;
    }

    public GenesisArchetypeRequirement getRequirement() {
        return requirement;
    }

    public GenesisObject[] getObjects() {
        return objects;
    }
}
