package com.uk.genesis.ant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import com.uk.genesis.model.GenesisObject;
import com.uk.genesis.model.GenesisObjectNotFoundException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

/**
 * Creates a filterset using the provided objects. This filterset can then
 * be used in various filtering operations.
 * 
 * @author paul.jones
 */
public class CreateFilterSetUsingObjects extends BaseGenesisTask {

    private String filterSetId;
    private List<GenesisObjectReference> objects;

    public void init() {
        objects = new ArrayList<GenesisObjectReference>();
    }

    public void setFiltersetid(String filterSetId) {
        this.filterSetId = filterSetId;
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

    public void execute() throws BuildException {
        validate();

        FilterSet filterSet = new FilterSet();

        // Add the global configuration
        try {
            Properties globalProps = getGenesisLoader().getModelReader().getGlobalContentAsProperties();
            filterSet.addConfiguredFilterSet(getFilterForProperties(globalProps));
        } catch (IOException ex) {
            throw new BuildException("Failed to load global configuration information", ex, getLocation());
        } catch (ModelException ex) {
            throw new BuildException("Failed to load global configuration information", ex, getLocation());
        }

        // Work through each object
        for (GenesisObjectReference ref : objects) {
            if (ref.isValid(getProject())) {
                try {
                    GenesisObjectType type = getGenesisLoader().getModelReader().findSingleObjectType(ref.getType());
                    if (type == null) {
                        throw new BuildException("Type " + ref.getType() + " is invalid", getLocation());
                    }

                    GenesisObject obj = type.getInstance(ref.getName());

                    // Now add filter sets for each of the files in the hierarchy
                    GenesisObject current = obj;
                    while (current != null) {
                        filterSet.addConfiguredFilterSet(getFilterForObject(current));

                        current = current.getParent();
                    }
                } catch (ModelException ex) {
                    throw new BuildException("Failed to find type " + ref.getType() + " - " + ex.getMessage(), ex, getLocation());
                } catch (GenesisObjectNotFoundException ex) {
                    throw new BuildException("Failed to find object " + ref.getName() + " of type " + ref.getType() + " - " + ex.getMessage(), ex,
                            getLocation());
                } catch (IOException ex) {
                    throw new BuildException("Failed to load content for object " + ref.getName() + " of type " + ref.getType() + " - " + ex.getMessage(), ex,
                            getLocation());
                }
            }
        }

        getProject().addReference(filterSetId, filterSet);
    }

    protected void validate() {
        super.validate();

        if (filterSetId == null) {
            throw new BuildException("property filtersetid is required", getLocation());
        }
    }

    protected FilterSet getFilterForObject(GenesisObject object) throws IOException {
        return getFilterForProperties(object.getContentAsProperties());
    }

    protected FilterSet getFilterForProperties(Properties props) {
        FilterSet result = new FilterSet();

        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();

            result.addFilter(propName, props.getProperty(propName));
        }

        return result;
    }
}
