package com.uk.genesis.ant;

import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.types.FilterSet;

/**
 * Creates filters out of designated object in Genesis
 * Configuration Hierarchy.
 * @author Mike Mochan
 * @author Hussein Badakhchani
 *
 */

public class RetrieveObjectProperties extends BaseGenesisTask {

    private String filtername;
    private String prefix;

    public void execute() {

        FilterSet fs = (FilterSet) getProject().getReference(filtername);

        // Add the global configuration

        Map<?, ?> map = fs.getFilterHash();
        

        for (Iterator<?> it = map.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            Object value = map.get(key);
            // Add to Global properties
            addToProject(key.toString(), value.toString());
//                System.out.println("found map key : " + key.toString() + " with value of : " + value.toString());
        }
    }

    public void addToProject(String key, String value) {
        getProject().setProperty(getPrefix() + key, value);
    }

    public String getFiltername() {
        return filtername;
    }

    public void setFiltername(String filtername) {
        this.filtername = filtername;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

