package com.uk.genesis.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * Task for finding all tokens in a given set of resource collections. This can
 * be used for auditing substituted files to ensure that the process has completed
 * successfully. Note that due to the high possibility of false-matches (any line
 * that contains two "@" symbols will generate a result. Many scenarios could lead
 * to these false matches, such as two email addresses on a single line. 
 * 
 * Added failOnError - Mike Mochan 20th April 2009
 * 
 * @author paul.jones
 */
public class FindTokens
        extends Task {
	
	
	private boolean failOnError;


	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}


	private List<ResourceCollection> resources;

    public FindTokens() {
        resources = new ArrayList<ResourceCollection>();
    }

    public void add(ResourceCollection col) {
        this.resources.add(col);
    }

    @Override
    public void execute() {
        try {
            // Work through each of the supplied resources, and search for tokens
            for (ResourceCollection col : resources) {
                for (Iterator<?> resourceIt = col.iterator(); resourceIt.hasNext();) {
                    Resource res = (Resource) resourceIt.next();

                    processStream(res.getName(), res.getInputStream());
                }
            }
        } catch (IOException ex) {
            throw new BuildException("Failed to process resource", ex, getLocation());
        }
    }
    
    private void processStream(String resName, InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        Set<String> tokens = new HashSet<String>();

        // Process each of the lines
        while ((line = br.readLine()) != null) {
            // Try to find @ tokens within the line.
            int curPos = 0;
            int curStart = -1;
            int atPos;

            while ((atPos = line.indexOf('@', curPos)) != -1) {
                if (curStart == -1) {
                    // Token start
                    curStart = atPos;
                } else {
                    // Token end
                    String token = line.substring(curStart, atPos + 1);
                    if (!tokens.contains(token)) {
                    	if (token.contains("@Environment.") || (token.contains("@Server.")) || (token.contains("@Global.")) || (token.contains("@Project.") || (token.contains("@UNDEFINED")))) {
                            tokens.add(token);
                    	}
                    }
                    curStart = atPos;
                }

                curPos = atPos + 1;
            }
        }
        
        // Dump the tokens
        String[] tokenList = tokens.toArray(new String[tokens.size()]);
        Arrays.sort(tokenList);
        if (tokenList.length > 0 )  {
            log(resName + ":", Project.MSG_INFO);

            for (String token : tokenList) {
                log("  " + token, Project.MSG_INFO);
            }
        }
        if( tokenList.length > 0 && failOnError == true) {
        	System.out.println("BUILD FAILD");
            Runtime.getRuntime().exit(1);
        }
    }
}
