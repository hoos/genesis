package com.voca.middleware.genesis;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSet;



/**
 * 
 * @author Mike Mochan
 *
 */

public class IncrementValue extends Task {
	
	private String property;
	private String filtername;
	private String chkProp;
	private int newVal;
	
private List<PropertyMatch> matches = new ArrayList<PropertyMatch>();

	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}		
	public String getFiltername() {
		return filtername;
	}
	public void setFiltername(String filtername) {
		this.filtername = filtername;
	}
	
	public void execute() {		
		FilterSet fs = (FilterSet) getProject().getReference(filtername);
	    Map map = fs.getFilterHash();
	    int EnvNo = 0;

	    // Check the value of $Environment.EnvNo}
	    for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Object value = map.get(key);
            
            if(chkProp.matches(key.toString())) {
                EnvNo = Integer.valueOf(value.toString()).intValue();
            }        
        }
	    
	    for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            Object value = map.get(key);
            
            if(property.matches(key.toString())) {
                if (EnvNo == 2) {
                    System.out.println(" Configuring dual environment");
                    value = newVal;
                    fs.addFilter(property, value.toString());
                    System.out.println(" Configuring administration-port as : " + value);
                } else {
                    System.out.println(" Single hosted environment ");
                }
                
            }
        }
	}

    public String getChkProp() {
        return chkProp;
    }
    public void setChkProp(String chkProp) {
        this.chkProp = chkProp;

}
    public int getNewVal() {
        return newVal;
    }
    public void setNewVal(int newVal) {
        this.newVal = newVal;
    }
}