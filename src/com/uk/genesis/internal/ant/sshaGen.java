package com.uk.genesis.internal.ant;

import com.octetstring.vde.util.PasswordEncryptor;

import java.util.Iterator;
import java.util.Map;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

import java.security.NoSuchAlgorithmException;

/**
 * Generates SHAA password in the format required for 
 * WebLogic DefaultAuthenticatorInit.ldift file.
 * Iterates through a filter set matching clear text passwords.
 * When a match is found the password is encrypted and the new
 * filter is added. 
 * @author Mike Mochan
 * @author Hussein Badakhchani
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
	    Map<?, ?> map = fs.getFilterHash();
    	for (Iterator<?> it = map.keySet().iterator(); it.hasNext(); ) {
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
