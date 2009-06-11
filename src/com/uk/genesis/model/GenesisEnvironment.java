package com.uk.genesis.model;

import java.io.File;

import org.w3c.dom.Element;

/**
 * Class providing details of a given genesis environment.
 * 
 * @author paul.jones
 */
public class GenesisEnvironment {
	private File genesisRoot;
	private Element genesisModel;
	private Element genesisArchetypes;
	
	public GenesisEnvironment(File genesisRoot, Element genesisModel, Element genesisArchetypes) {
		this.genesisRoot = genesisRoot;
		this.genesisModel = genesisModel;
		this.genesisArchetypes = genesisArchetypes;
	}
	
	public File getGenesisRoot() {
		return genesisRoot;
	}
	
	public Element getGenesisModel() {
		return genesisModel;
	}
	
	public Element getGenesisArchetypes() {
		return genesisArchetypes;
	}
}
