package com.uk.genesis.internal.ant;

import java.util.Iterator;
import java.util.Map;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

import com.uk.genesis.Base64;

/**
 * Generates SHA1 password required for WebLogic Portal. Accepts cleartext source password and property to store result
 * @author Mike Mochan
 * @author Hussein Badakhchani
 *
 */
public class Sha1Gen extends Task {
	
	private String sourcepwd;
	private String property;
	private String filtername;

	public String getSourcepwd() {
		return sourcepwd;
	}
	public void setSourcepwd(String sourcepwd) {
		this.sourcepwd = sourcepwd;
	}
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
	    Map<?, ?> map = fs.getFilterHash();
    	for (Iterator<?> it = map.keySet().iterator(); it.hasNext(); ) {
    	    Object key = it.next();
    	    Object value = map.get(key);
    	    
    	    if(sourcepwd.matches(key.toString())) {
	    	    System.out.println("Encrypting " + value + " to SHA1");
	    	    try {
	    	    	fs.addFilter(property , ("{SHA1}" + encrypt(value.toString())));
	    	    }catch (NoSuchAlgorithmException nsa) {
	    	    	throw new BuildException("No such Algorithm", getLocation());
	    	    }
	    	}
    	}
	}	 		
    public synchronized static String encrypt(String plaintext) throws NoSuchAlgorithmException
    {		
	    MessageDigest md = null;
	    try
	    {
	      md = MessageDigest.getInstance("SHA"); 
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	      throw new NoSuchAlgorithmException(e.getMessage());
	    }
	    try
	    {
	      md.update(plaintext.getBytes("UTF-8")); 
	    }
	    catch(IOException e)
	    {
	      throw new NoSuchAlgorithmException(e.getMessage());
	    }

	    byte raw[] = md.digest(); 
	    String hash = Base64.encodeBytes(raw); 
	    return hash; 
	}

}
