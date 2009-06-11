package com.uk.genesis.archetype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.uk.genesis.GenesisNamespaceContext;
import com.uk.genesis.InvalidGenesisConfigurationException;
import com.uk.genesis.model.GenesisObjectType;
import com.uk.genesis.model.ModelException;
import com.uk.genesis.model.ModelReader;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of the GenesisAction.
 * 
 * @author paul.jones
 */
public class GenesisArchetypeImpl implements GenesisArchetype {
	private final File root;
	private final Element actionEl;
	private final GenesisArchetypeRequirement[] requirements;
	private final GenesisArchetypeOperation[] operations;
	
	public GenesisArchetypeImpl(final File root, final ModelReader modelReader, final Element actionEl) 
			throws InvalidGenesisConfigurationException {
		this.root = root;
		this.actionEl = actionEl;
		
		requirements = parseRequirements(modelReader);
		operations = parseOperations();
	}

	public String getName() {
		return actionEl.getAttribute("name");
	}

	public File getBuildFile() {
		return new File(root, actionEl.getAttribute("buildfile")).getAbsoluteFile();
	}
	
	public GenesisArchetypeRequirement[] getRequirements() {
		return requirements;
	}
	
	public GenesisArchetypeOperation[] getOperations() {
		return operations;
	}
	
	private GenesisArchetypeRequirement[] parseRequirements(ModelReader modelReader) 
			throws InvalidGenesisConfigurationException {
		try {
			XPathExpression expr = getXPath("genesis:requires");
			
			// Retrieve and convert the nodes
			NodeList nodes = (NodeList) expr.evaluate(actionEl, XPathConstants.NODESET);
			List<GenesisArchetypeRequirement> result = new ArrayList<GenesisArchetypeRequirement>();
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element el = (Element) nodes.item(i);
				String typeName = el.getAttribute("type");
				GenesisObjectType type = modelReader.findSingleObjectType(typeName);
				String qnameProperty = el.getAttribute("qnameproperty");
				String nameProperty = el.getAttribute("nameproperty");
				
				if (type == null) {
					throw new InvalidGenesisConfigurationException("Type " + typeName + " was not valid in declaration for action " + getName());
				}
				
				GenesisArchetypeRequirement requirement = new GenesisArchetypeRequirement(type, qnameProperty, nameProperty);
				
				result.add(requirement);
			}
			return result.toArray(new GenesisArchetypeRequirement[result.size()]);
		} catch (XPathExpressionException ex) {
			throw new InvalidGenesisConfigurationException("Failed to compile xpath", ex);
		} catch (ModelException ex) {
			throw new InvalidGenesisConfigurationException("Failed to match requirement type", ex);
		}
	}
	
	private GenesisArchetypeOperation[] parseOperations() 
			throws InvalidGenesisConfigurationException {
		try {
			XPathExpression expr = getXPath("genesis:operation");
			
			// Retrieve and convert the nodes
			NodeList nodes = (NodeList) expr.evaluate(actionEl, XPathConstants.NODESET);
			List<GenesisArchetypeOperation> result = new ArrayList<GenesisArchetypeOperation>();
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element el = (Element) nodes.item(i);
				String name = el.getAttribute("name");
				String target = el.getAttribute("target");
				
				GenesisArchetypeOperation op = new GenesisArchetypeOperation(name, target);
				
				result.add(op);
			}
			return result.toArray(new GenesisArchetypeOperation[result.size()]);
		} catch (XPathExpressionException ex) {
			throw new InvalidGenesisConfigurationException("Failed to compile xpath", ex);
		}
	}
	
	private XPathExpression getXPath(String expr) 
			throws XPathExpressionException {
		GenesisNamespaceContext nsCtx = new GenesisNamespaceContext();
		nsCtx.addNamespace("genesis", "http://genesis.uk.com/schemas/1.0/genesis-root");
		
		XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    xpath.setNamespaceContext(nsCtx);
    return xpath.compile(expr);
	}
}
