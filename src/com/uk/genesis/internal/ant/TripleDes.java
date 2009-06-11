package com.uk.genesis.internal.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import weblogic.security.internal.*;
import weblogic.security.internal.encryption.ClearOrEncryptedService;
import weblogic.security.internal.encryption.EncryptionService;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSet;

/**
 * Generates 3DES passwords. Encrypts plaintext passwords in Archetype source.
 * Requires encryptionservice parameter specifying the WebLogic "../domain/security" directory
 * e.g. 
 *  <tripledes encryptionservice="./archetypes/wlserver92/security" filtername="genesis.filter.properties">
 * and pattern match property
 * e.g.
 * <match pattern=".*KeyPassPhrase" />  
 *    
 * @author Mike Mochan
 *
 */

public class TripleDes extends Task {
	private String encryptionservice;
	private String filtername;

	private List<PropertyMatch> matches = new ArrayList<PropertyMatch>();
	
	
	public String getEncryptionservice() {
		return encryptionservice;
	}
	public void setEncryptionservice(String encryptionservice) {
		this.encryptionservice = encryptionservice;
	}

	public String getFiltername() {
		return filtername;
	}
	public void setFiltername(String filtername) {
		this.filtername = filtername;
	}
	
	public void addConfiguredMatch(PropertyMatch propMatch) {
		if (propMatch.getPattern() == null) {
			throw new BuildException("match must specify pattern", getLocation());
		}
		matches.add(propMatch);
	}

	public void execute() {
		if (getFiltername() == null) {
			throw new BuildException("filtername must be set", getLocation());
		}
		if (getEncryptionservice() == null) {
			throw new BuildException("encryptionservice must be set", getLocation());
		}
		if (!new File(getEncryptionservice()).exists()) {
			throw new BuildException("encryptionservice directory must exist", getLocation());
		}
		
		EncryptionService es = SerializedSystemIni.getEncryptionService(encryptionservice);
		ClearOrEncryptedService ces = new ClearOrEncryptedService(es);
		
		FilterSet fs = (FilterSet) getProject().getReference(filtername);	

	    Map<?, ?> map = fs.getFilterHash();
	    
	    for (Iterator<PropertyMatch> pm = matches.iterator(); pm.hasNext();) {
	    	PropertyMatch PatternMatch = pm.next();
	    	Pattern GlobalPattern = Pattern.compile(PatternMatch.getPattern());
	    //	System.out.println("Matching : " + GlobalPattern.toString());

	    	for (Iterator<?> it = map.keySet().iterator(); it.hasNext(); ) {
	    	    Object key = it.next();
	    	    Object value = map.get(key);

    	    	if(GlobalPattern.matcher(key.toString()).matches()){
    	    		fs.addFilter((key.toString() + "Encrypted"), ces.encrypt(value.toString()));
    	    	}
    	    }
	    }
	}
}
