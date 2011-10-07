package com.uk.genesis.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EnumUtils {
    public static GenesisObject[] enumerateChildObjectsOfType(final GenesisObject current, final GenesisObjectType desiredType)
            throws ModelException {
        GenesisObjectType curType = current.getType();
        GenesisObjectType desiredParentType = desiredType.getParent();

        // If there is no desired parent type, then something is a bit wrong.
        if (desiredParentType == null) {
            return new GenesisObject[0];
        }

        // First see if we're the direct parent of the type. If we are, just list and return
        if (curType.getQualifiedName().equals(desiredParentType.getQualifiedName())) {
            return current.getChildren(desiredType);
        }

        // We aren't a direct parent. Work out what the next step in the chain is, and enumerate those.
        return enumerateChildObjectsOfType(current, curType.getChildren(), desiredType);
    }

    public static GenesisObject[] enumerateChildObjectsOfType(final GenesisObject parentParentInstance, final GenesisObjectType[] possibleParents, final GenesisObjectType desiredType)
            throws ModelException {
        GenesisObjectType nextType = findRelevantParent(possibleParents, desiredType);

        // Enumerate all instances of the next type
        List<GenesisObject> result = new ArrayList<GenesisObject>();
        for (GenesisObject relevantRootObj : nextType.getAllChildInstances(parentParentInstance)) {
            result.addAll(Arrays.asList(enumerateChildObjectsOfType(relevantRootObj, desiredType)));
        }

        return result.toArray(new GenesisObject[result.size()]);
    }

    public static GenesisObjectType findRelevantParent(final GenesisObjectType[] types, final GenesisObjectType child)
            throws ModelException {
        GenesisObjectType curParent = child.getParent();
        while (curParent != null) {
            for (GenesisObjectType type : types) {
                if (curParent.getQualifiedName().equals(type.getQualifiedName())) {
                    return curParent;
                }
            }

            curParent = curParent.getParent();
        }

        // We didn't find anything that matched
        throw new ModelException("Couldn't find an appropriate step to reach " + child.getQualifiedName());
    }
}
