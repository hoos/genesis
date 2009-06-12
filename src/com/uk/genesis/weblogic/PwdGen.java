package com.uk.genesis.weblogic;

import java.util.Random;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSet;

/**
 * Generates random passwords. Accepts password length and property type
 * @author Mike Mochan
 *
 */

public class PwdGen extends Task {
	
	private String property;
	private String filtername;
	private int length;

	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
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
		String genpwd = "";
		FilterSet fs = (FilterSet) getProject().getReference(filtername);	
	    //Generate random password and set property
	    genpwd = getRandomPassword(length);
	    fs.addFilter(property, genpwd);
	    System.out.println("Generated Password : " + genpwd + "\n");
	}
    public String getRandomPassword(int length) {
  	  StringBuffer buffer = new StringBuffer();
  	  Random random = new Random();
  	  
  	  char[] chars = new char[] {	'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
  			  						'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
  			  						'0', '1', '2', '3', '4', '5', '6','7','8','9'};
  	  for ( int i = 0; i < length; i++ ) {
  	    buffer.append(chars[random.nextInt(chars.length)]);
  	  }
  	  return buffer.toString();
    }

}
