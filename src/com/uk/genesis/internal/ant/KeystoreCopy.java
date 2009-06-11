package com.uk.genesis.internal.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

/**
 * Matches environment keystore files and performs copy based on src / dest / pattern match.
 * @author Mike Mochan
 *
 */
public class KeystoreCopy extends Task {
	private String message;
	private String filtername;
	private String destfile;	
	private String srcfile;

	
	private List<PropertyMatch> matches = new ArrayList<PropertyMatch>();

	public void execute() {
		String munge = "";
		
		FilterSet fs = (FilterSet) getProject().getReference(filtername);	
	    Map<?, ?> map = fs.getFilterHash();
	   
	    for (Iterator<PropertyMatch> pm = matches.iterator(); pm.hasNext();) {
	    	PropertyMatch PatternMatch = pm.next();
	    	Pattern GlobalPattern = Pattern.compile(PatternMatch.getPattern());
	    	System.out.println("Matching : " + GlobalPattern.toString());
	    	for (Iterator<?> it = map.keySet().iterator(); it.hasNext(); ) {
	    	    Object key = it.next();
	    	    Object value = map.get(key);

    	    	if(GlobalPattern.matcher(key.toString()).matches()){
    	    	    munge = value.toString();
    	    	    StringTokenizer st = new StringTokenizer(munge, "/");    
    	    	    String split = "";
    	    	    for( int x = 0; st.hasMoreTokens(); x++) {
    	    	    	split = st.nextElement().toString();
	        	    	if ( split.endsWith("jks")) {
	        	    		File in = new File(srcfile + "/" + split);
	        	    		File out = new File(destfile + "/" + split);	        	    		
	        	    		try {
	        	    			copyFile(in, out);
	        	    		} catch (FileNotFoundException fnf) {
	        	    			System.out.println("keystore file " + split + " cannot be found");
	        	    		} catch (Exception e) {
	        	    			e.printStackTrace();
	        	    		}
    	    	    	}
    	    	    }
    	    	}
	    	}
	    }
	}
    public void copyFile(File in, File out) throws Exception {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while((i=fis.read(buf))!=-1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }
    
	public String getFiltername() {
		return filtername;
	}
	public void setFiltername(String filtername) {
		this.filtername = filtername;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void addConfiguredMatch(PropertyMatch propMatch) {
		if (propMatch.getPattern() == null) {
			throw new BuildException("match must specify pattern", getLocation());
		}
		matches.add(propMatch);
	}
	public String getDestfile() {
		return destfile;
	}
	public void setDestfile(String destfile) {
		this.destfile = destfile;
	}
	public String getSrcfile() {
		return srcfile;
	}
	public void setSrcfile(String srcfile) {
		this.srcfile = srcfile;
	}
}
