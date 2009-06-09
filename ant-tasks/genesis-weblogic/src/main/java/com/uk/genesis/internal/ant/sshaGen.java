package com.uk.genesis.internal.ant;

import com.octetstring.vde.util.PasswordEncryptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

import sun.misc.BASE64Encoder;

/**
 * Generates SHAA password. Required format for WebLogic DefaultAuthenticatorInit.ldift.
 * accepts cleartext password encrypts and returns to property. 
 * @author Mike Mochan
 *
 */

public class sshaGen extends Task {
	
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
	    Map map = fs.getFilterHash();
    	for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
    	    Object key = it.next();
    	    Object value = map.get(key);
    	    
    	    if(sourcepwd.matches(key.toString())) {
	    	    System.out.println("Encrypting " + value + " to SSHA");
	    	    try {
	    	    	fs.addFilter(property , ("{ssha}" + encrypt(value.toString())));
	    	    }catch (NoSuchAlgorithmException nsa) {
	    	    	throw new BuildException("No such Algorithm", getLocation());
	    	    }
	    	}
    	}
	}	 		
    public synchronized static String encrypt(String plaintext) throws NoSuchAlgorithmException
    {		
        String sshaEncrypt = "";
        sshaEncrypt = PasswordEncryptor.doSSHA(plaintext);
	    return sshaEncrypt; 
	}

}
