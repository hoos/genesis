package com.uk.genesis.ant;

import java.io.IOException;

import com.uk.genesis.GenesisLoader;
import com.uk.genesis.InvalidGenesisConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Base class implemented by all classes that support genesis.
 * @author paul.jones
 *
 */
public class BaseGenesisTask extends Task {

    private String genesisXml;
    private GenesisLoader loader = null;

    public void setGenesisxml(String genesisXml) {
        this.genesisXml = genesisXml;
    }

    protected void validate() {
        if (this.genesisXml == null) {
            throw new BuildException("property genesisxml is required", getLocation());
        }
    }

    protected GenesisLoader getGenesisLoader() {
        if (loader == null) {
            loader = new GenesisLoader();
            try {
                loader.load(this.genesisXml);
            } catch (IOException ex) {
                throw new BuildException("Failed to load " + genesisXml + " as Genesis root: " + ex.getMessage(), ex, getLocation());
            } catch (InvalidGenesisConfigurationException ex) {
                throw new BuildException("Failed to load " + genesisXml + " as Genesis root: " + ex.getMessage(), ex, getLocation());
            }
        }

        return loader;
    }
}
