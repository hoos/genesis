package com.uk.genesis.archetype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.uk.genesis.GenesisNamespaceContext;
import com.uk.genesis.InvalidGenesisConfigurationException;
import com.uk.genesis.model.GenesisEnvironment;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelReader;

/**
 * Implementation of the ArchetypeFactory.
 * 
 * @author paul.jones
 */
public class ArchetypeFactoryImpl implements ArchetypeFactory {
	private GenesisArchetype[] archetypes;
	
	/**
	 * Creates a new ArchetypeFactory implementation using the given environment.
	 * @param env the environment.
	 */
	public ArchetypeFactoryImpl(GenesisEnvironment env, ModelReader modelReader) 
			throws InvalidGenesisConfigurationException {
		// If there was no archetype declaration, then just store an empty list of actions
		if (env.getGenesisArchetypes() == null) {
			archetypes = new GenesisArchetype[0];
			return;
		}
		
		// Parse the actions
		parseActions(env.getGenesisRoot(), modelReader, env.getGenesisArchetypes());
	}
	
	public GenesisArchetype[] getArchetypes() {
		return archetypes;
	}
	
	/**
	 * Parses the actions contained under the given root.
	 * @param root the root.
	 * @param modelReader the model reader to use to resolve any requirement types.
	 * @param actionsContainer the container for the actions.
	 */
	private void parseActions(File root, ModelReader modelReader, Element actionsContainer) 
			throws InvalidGenesisConfigurationException {
		try {
			GenesisNamespaceContext nsCtx = new GenesisNamespaceContext();
			nsCtx.addNamespace("genesis", "http://genesis.uk.com/schemas/1.0/genesis-root");
			
			XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    xpath.setNamespaceContext(nsCtx);
	    XPathExpression expr = xpath.compile("genesis:archetype");
			
			// Retrieve and convert the nodes
			NodeList nodes = (NodeList) expr.evaluate(actionsContainer, XPathConstants.NODESET);
			List<GenesisArchetype> result = new ArrayList<GenesisArchetype>();
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element el = (Element) nodes.item(i);
				
				result.add(buildAction(root, modelReader, el));
			}
			archetypes = result.toArray(new GenesisArchetype[result.size()]);
		} catch (XPathExpressionException ex) {
			throw new InvalidGenesisConfigurationException("Failed to compile xpath", ex);
		}
	}
	
	/**
	 * Builds an action instance from the given xml element.
	 * @param root the file system root that any paths specified in the action should start from.
	 * @param modelReader the model reader to use to resolve any requirement types.
	 * @param action definition of the action.
	 * @return the action.
	 */
	private GenesisArchetype buildAction(File root, ModelReader modelReader, Element action) 
			throws InvalidGenesisConfigurationException {
		return new GenesisArchetypeImpl(root, modelReader, action);
	}
}
