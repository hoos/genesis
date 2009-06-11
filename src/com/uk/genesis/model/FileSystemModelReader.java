package com.uk.genesis.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.uk.genesis.GenesisNamespaceContext;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Base implementation of the model reader working with file system based
 * models (eg properties files or xml files).
 * 
 * @author paul.jones
 */
public class FileSystemModelReader implements ConfigurableModelReader {
	private Element model;
	private File root;
	
	public FileSystemModelReader() {
	}
	
	public void configure(GenesisEnvironment env) {
		model = env.getGenesisModel();
		root = env.getGenesisRoot();
	}
	
	public GenesisObjectType[] getRootObjectTypes() throws ModelException {
		return getObjects(model, null, true);
	}
	
	public GenesisObjectType findSingleObjectType(String path) 
			throws ModelException {
		// Transform the path expression to a XPath query
		String[] pathComponents = path.split("/");
		StringBuilder pathBuilder = new StringBuilder();
		for (int i = 0; i < pathComponents.length; ++i) {
			if (pathBuilder.length() > 0) {
				pathBuilder.append("/");
			}
			if (pathComponents[i].length() > 0) {
				pathBuilder.append("model:object[@name=\"" + pathComponents[i] + "\"]");
			}
		}
		
		try {
			// Get the xpath and perform the selection
			String queryPath = pathBuilder.toString();
			XPathExpression xpath = getXModelXPath(queryPath);
			Element object = (Element) xpath.evaluate(model, XPathConstants.NODE);
			if (object == null) {
				return null;
			}
			
			return createTypeInstance(object, null, object.getParentNode() == model);
		} catch (XPathExpressionException ex) {
			throw new ModelException("Exception executing XPath query", ex);
		}
	}
	
	public File getGlobalContentLocation() 
			throws ModelException {
		try {
			XPathExpression xpath = getXModelXPath("model:global");
			Element global = (Element) xpath.evaluate(model, XPathConstants.NODE);
			if (global == null) {
				return null;
			}
			String attr = global.getAttribute("filename");
			if (attr == null || attr.length() == 0) {
				return null;
			}
			
			return new File(root, attr);
		} catch (XPathExpressionException ex) {
			throw new ModelException("Exception executing XPath query", ex);
		}
	}
	
	public Properties getGlobalContentAsProperties() 
			throws IOException, ModelException {
		Properties result = new Properties();
		if (getGlobalContentLocation() != null) {
			result.load(new FileInputStream(getGlobalContentLocation()));
		}
		
		return result;
	}
	
	protected File getRoot() {
		return root;
	}
	
	protected FileSystemGenesisObjectType[] getObjects(Element parentEl, FileSystemGenesisObjectType parent, boolean isRoot)
			throws ModelException {
		try {
			XPathExpression xpath = getXModelXPath("model:object");
			
			// Retrieve and convert the nodes
			NodeList nodes = (NodeList) xpath.evaluate(parentEl, XPathConstants.NODESET);
			List<GenesisObjectType> result = new ArrayList<GenesisObjectType>();
			for (int i = 0; i < nodes.getLength(); ++i) {
				Element el = (Element) nodes.item(i);
				
				result.add(createTypeInstance(el, parent, isRoot));
			}
			return result.toArray(new FileSystemGenesisObjectType[0]);
		} catch (XPathExpressionException ex) {
			throw new ModelException("Failed to execute XPath", ex);
		}
	}
	
	/**
	 * Retrieves the namespace that model elements should be found in. This method
	 * should be overriden in subclasses that provide alternate namespaces.
	 * @return the namespace.
	 */
	protected String getModelNamespace() {
		return "http://genesis.uk.com/schemas/1.0/genesis-fs-model";
	}
	
	/**
	 * Retrieves a XPath instance that can be used to find elements with the 
	 * provided path. This method primary constructs the XPath will valid
	 * namespaces.
	 * @param path
	 * @return
	 */
	protected XPathExpression getXModelXPath(String path) throws XPathExpressionException {
		// Find all of the xmodel nodes
		GenesisNamespaceContext nsCtx = new GenesisNamespaceContext();
		nsCtx.addNamespace("genesis", "http://genesis.uk.com/schemas/1.0/genesis-root");
		nsCtx.addNamespace("model", getModelNamespace());
		
		XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    xpath.setNamespaceContext(nsCtx);
    XPathExpression expr = xpath.compile(path);
		
		return expr;
	}
	
	/**
	 * Creates an instance of the type for the given element, parent and root. This can be overriden
	 * by subclasses that wish to provide their own type instances.
	 * @param el the xml element that is being modeled.
	 * @param parent the parent type.
	 * @param isRoot whether this is a root type.
	 * @return the type instance.
	 */
	protected FileSystemGenesisObjectType createTypeInstance(Element el, FileSystemGenesisObjectType parent, 
																													 boolean isRoot) {
		return new FileSystemGenesisObjectType(el, parent, isRoot);
	}
	
	/**
	 * Implementation of the genesis object for xml backed items.
	 */
	protected class FileSystemGenesisObjectType implements GenesisObjectType {
		private Element object;
		private FileSystemGenesisObjectType parent;
		private boolean isRoot;
		
		public FileSystemGenesisObjectType(Element object, FileSystemGenesisObjectType parent, boolean isRoot) {
			this.object = object;
			this.parent = parent;
			this.isRoot = isRoot;
		}
		
		public FileSystemGenesisObjectType[] getChildren() 
				throws ModelException {
			return getObjects(object, this, false);
		}

		public String getName() {
			return object.getAttribute("name");
		}
		
		public String getQualifiedName() {
			GenesisObjectType curParent = getParent();
			String base = "";
			if (curParent != null) {
				base = curParent.getQualifiedName();
			}
			
			return base + "/" + getName();
		}

		public FileSystemGenesisObjectType getParent() {
			if (!isRoot && parent == null) {
				// We need to generate our parent
				parent = createTypeInstance((Element) object.getParentNode(), null, 
						object.getParentNode().getParentNode().equals(model));
			}
			
			return parent;
		}

		public FileSystemGenesisObject getInstance(String path) 
				throws GenesisObjectNotFoundException {
			// Find our name, and the parent path name
			int lastSlash = path.lastIndexOf('/');
			String parentPath = null;
			String name = null;
			if (lastSlash == -1) {
				name = path;
			} else if (lastSlash == 0) {
				name = path.substring(1);
			} else {
				parentPath = path.substring(0, lastSlash);
				name = path.substring(lastSlash + 1);
			}
			
			// See if we're at the top of the tree or not
			FileSystemGenesisObjectType curParent = getParent();
			if (curParent == null) {
				if (parentPath != null) {
					// We don't have a parent, but the path is asking for one. The request
					// path was invalid.
					throw new IllegalArgumentException("Requested path is too deep. Cannot find " 
							+ path + " in " + getQualifiedName());
				}
				
				// We just need to match from the genesis root
				return getInstance(name, root, null);
			} else {
			
				if (parentPath == null) {
					// We have a parent, but no path to resolve it. The request path was
					// invalid.
					throw new IllegalArgumentException("Requested path is too shallow. No path for " 
							+ getQualifiedName());
				}
				
				// Retrieve the parent object, then request our instance using it
				// as a base.
				FileSystemGenesisObject parentObject = curParent.getInstance(parentPath);
				return getInstance(name, parentObject.getPath(), parentObject);
			}
		}
		
		public String getPath() {
			return object.getAttribute("path");
		}
		
		public GenesisObject[] getAllChildInstances(GenesisObject parent) 
				throws ModelException {
			// Validate the parent
			if (isRoot && parent != null) {
				throw new ModelException("Root type expects a null parent request");
			}
			if (!isRoot) {
				GenesisObjectType expectedType = getParent();
				GenesisObjectType actualType = parent.getType();
				
				if (!expectedType.getQualifiedName().equals(actualType.getQualifiedName())) {
					throw new ModelException("Parent was not of expected type " + expectedType.getQualifiedName());
				}
			}
			
			if (parent != null) {
				// Convert the parent
				FileSystemGenesisObject fileSysParent = (FileSystemGenesisObject) parent;
				return getAllInstancesAtPath(fileSysParent, fileSysParent.getPath());
			} else {
				return getAllInstancesAtPath(null, getRoot());
			}
		}
		
		protected Element getElement() {
			return object;
		}
		
		protected String getFileName() {
			return object.getAttribute("filename");
		}
		
		protected FileSystemGenesisObject getInstance(String name, File basePath, FileSystemGenesisObject parentObject) 
				throws GenesisObjectNotFoundException {
			// Build our path if necessary
			String pathExtension = getPath();
			File objPath = basePath;
			if (pathExtension != null) {
				// Replace the name token and generate the new path
				pathExtension = pathExtension.replaceAll("\\$\\{name\\}", name);
				objPath = new File(basePath, pathExtension);
			}
			
			// Build our filename if necessary
			String filename = getFileName();
			File objFile = null;
			if (filename != null) {
				filename = filename.replaceAll("\\$\\{name\\}", name);
				objFile = new File(objPath, filename);
			}
			
			// If we have an associated file, verify it exists. If not, then just
			// verify the path exists.
			if (objFile != null && !objFile.exists()) {
				throw new GenesisObjectNotFoundException("File " + objFile + " doesn't exist");
			} else if (!objPath.exists()) {
				throw new GenesisObjectNotFoundException("Directory " + objPath + " doesn't exist");
			}
			
			return createInstance(name, parentObject, objPath, objFile);
		}
		
		protected FileSystemGenesisObject[] getAllInstancesAtPath(FileSystemGenesisObject parent, File basePath) {
			List<FileSystemGenesisObject> result = new ArrayList<FileSystemGenesisObject>();
			
			// Find all object names
			String[] instances = ObjectFinder.findObjectNames(basePath, getPath(), getFileName());
			for (String instance : instances) {
				try {
					result.add(getInstance(instance, basePath, parent));
				} catch (GenesisObjectNotFoundException ex) {
					throw new RuntimeException("Previously found object no longer available", ex);
				}
			}
			
			return result.toArray(new FileSystemGenesisObject[result.size()]);
		}
		
		protected FileSystemGenesisObject createInstance(String name, FileSystemGenesisObject parent, File objPath, File objFile) {
			return new FileSystemGenesisObject(this, parent, name, objPath, objFile);
		}
	}
	
	protected class FileSystemGenesisObject implements GenesisObject {
		private FileSystemGenesisObjectType type;
		private FileSystemGenesisObject parent;
		private String name;
		private File path;
		private File contentFile;
		
		public FileSystemGenesisObject(FileSystemGenesisObjectType type, FileSystemGenesisObject parent, String name, File path, File contentFile) {
			this.type = type;
			this.parent = parent;
			this.name = name;
			this.path = path;
			this.contentFile = contentFile;
		}
		
		public String getName() {
			return name;
		}

		public String getQualifiedName() {
			String base = "";
			if (parent != null) {
				base = parent.getQualifiedName();
			}
			
			return base + "/" + getName();
		}
		
		public GenesisObjectType getType() {
			return type;
		}
		
		public File getContentLocation() {
			return contentFile;
		}
		
		public Properties getContentAsProperties() throws IOException {
			Properties result = new Properties();
			if (getContentLocation() != null) {
				result.load(new FileInputStream(getContentLocation()));
			}
			
			return result;
		}
		
		public FileSystemGenesisObject getParent() {
			return parent;
		}
		
		public FileSystemGenesisObject[] getChildren() throws ModelException {
			List<FileSystemGenesisObject> result = new ArrayList<FileSystemGenesisObject>();
			
			// Work through each child type of ours, and find their paths
			for (FileSystemGenesisObjectType childType : type.getChildren()) {
				result.addAll(Arrays.asList(childType.getAllInstancesAtPath(this, path)));
			}
			
			return result.toArray(new FileSystemGenesisObject[result.size()]);
		}
		
		public GenesisObject[] getChildren(GenesisObjectType childType) throws ModelException {
			for (FileSystemGenesisObjectType knownChild : type.getChildren()) {
				if (knownChild.getQualifiedName().equals(childType.getQualifiedName())) {
					// We've found the child
					return knownChild.getAllInstancesAtPath(this, path);
				}
			}
			
			throw new IllegalArgumentException(childType.getQualifiedName() + " is not a known child of " 
					+ getType().getQualifiedName());
		}
		
		public void validate() throws GenesisObjectValidationException {
			// No validation available for File System models
		}
		
		protected File getPath() {
			return path;
		}
	}
}
