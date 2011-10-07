package com.uk.genesis.model;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Model reader for handling configuration hierarchies
 * where element content is defined in XML.
 *
 * @author paul.jones
 */
public class XmlModelReader
        extends FileSystemModelReader {
    /**
     * Overriden to handle XML differently to properties files.
     */
    @Override
    public Properties getGlobalContentAsProperties()
            throws IOException, ModelException {
                return loadXmlAsProperties(getGlobalContentLocation());
    }

    /**
     * Retrieves the namespace that model elements should be
     * found in. This method should be overriden in
     * subclasses that provide alternate namespaces.
     * @return the namespace.
     */
    protected String getModelNamespace() {
        return "http://genesis.uk.com/schemas/1.0/genesis-xml-model";
    }

    /**
     * Overriden to allow us to add additional features to the object type.
     */
    @Override
    protected FileSystemGenesisObjectType createTypeInstance(final Element el,
            final FileSystemGenesisObjectType parent, final boolean isRoot) {

                return new XmlModelGenesisObjectType(el, parent, isRoot);
    }

    /**
     * Loads an XML file as a properties map. Note that this is
     * different to the Properties.loadXml method, in that
     * it will take a normal XML file and transform it into a
     * series of properties, as opposed to requiring a specific format.
     * @param xmlLocation the location of the xml file. If this is null,
     * then the method will return an empty set of properties.
     * @return the loaded properties.
     */
    protected Properties loadXmlAsProperties(final File xmlLocation)
            throws IOException {
        Properties result = new Properties();
        if (xmlLocation == null) {
            return result;
        }

        try {
            // Load the XML for the item
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // Enable namespace support
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(xmlLocation);

            // Work through each node, building up the context of the properties
            loadElement(doc.getDocumentElement(), "", result);

            return result;
        } catch (ParserConfigurationException ex) {
            IOException ioex = new IOException("Failed to load XML content file");
            ioex.initCause(ex);

            throw ioex;
        } catch (SAXException ex) {
            IOException ioex = new IOException("Failed to load XML content file");
            ioex.initCause(ex);

            throw ioex;
        }
    }

    protected void loadElement(final Element el, final String context, final Properties result) {
        String newContext = context + el.getLocalName();

        // If we have a name attribute, then build that into our context
        String nameAttr = el.getAttribute("name");
        if (nameAttr != null && nameAttr.length() > 0) {
            newContext = newContext + "." + nameAttr;
        }

        // Get the children
        NodeList children = el.getChildNodes();

        // If this element has non-empty content, then add it as a property
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                content.append(child.getNodeValue());
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                // We've got child elements, so we don't
                // want to store any text against this key.
                content = null;
                break;
            }
        }
        if (content != null) {
            result.put(newContext, content.toString().trim());
        }

        // Build context for each of the children
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                loadElement((Element) child, newContext + ".", result);
            }
        }
    }

    protected class XmlModelGenesisObjectType extends FileSystemGenesisObjectType {
        public XmlModelGenesisObjectType(final Element object,
            final FileSystemGenesisObjectType parent, final boolean isRoot) {

            super(object, parent, isRoot);
        }

        /**
         * Overriden to allow us to add additional features to the object instance.
         */
        @Override
        protected FileSystemGenesisObject createInstance(final String name,
            final FileSystemGenesisObject parent, final File objPath, final File objFile) {

            return new XmlModelGenesisObject(this, parent, name, objPath, objFile);
        }

        /**
         * Retrieves the location of the schema for this object type,
         * or null if no schema is available. The schema path is
         * considered to be relative to the root of the genesis installation.
         * @return the location of the schema file.
         */
        public File getSchemaLocation() {
            String schemaLocation = getElement().getAttribute("schema");
            if (schemaLocation == null || schemaLocation.length() == 0) {
                return null;
            }

            return new File(XmlModelReader.this.getRoot(), schemaLocation);
        }
    }

    protected class XmlModelGenesisObject extends FileSystemGenesisObject {
        private XmlModelGenesisObjectType type;

        public XmlModelGenesisObject(final XmlModelGenesisObjectType type,
                final FileSystemGenesisObject parent, final String name,
                    final File path, final File contentFile) {

            super(type, parent, name, path, contentFile);

            this.type = type;
        }

        /**
         * Overriden to handle XML differently to properties files.
         */
        @Override
        public Properties getContentAsProperties() throws IOException {
            // Load the content as properties
            return loadXmlAsProperties(getContentLocation());
        }

        /**
         * Overridden to allow for schema based validation of genesis objects.
         * @throws GenesisObjectValidationException
         */
        @Override
        public void validate() throws GenesisObjectValidationException {
            super.validate();
            // Do any validation in the FileSystem model reader.
            // Note that if the validation in the file system
            // model reader was to be enhanced to do anything property
            // file specific, then this call may need to be removed.

            File schemaLocation = type.getSchemaLocation();
            if (schemaLocation == null) {
                return;	    // No validation to do
            }

            if (!schemaLocation.exists()) {
                throw new GenesisObjectValidationException("Schema file "
                                    + schemaLocation + " does not exist");
            }

            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema schema = factory.newSchema(schemaLocation);
                Validator validator = schema.newValidator();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder parser = dbf.newDocumentBuilder();
                org.w3c.dom.Document document = parser.parse(getContentLocation());
                validator.validate(new DOMSource(document));
            } catch (SAXException ex) {
                throw new GenesisObjectValidationException("Failed to validate object - " + ex.getMessage(), ex);
            } catch (ParserConfigurationException ex) {
                throw new GenesisObjectValidationException("Failed to validate object - " + ex.getMessage(), ex);
            } catch (IOException ex) {
                throw new GenesisObjectValidationException("Failed to validate object - " + ex.getMessage(), ex);
            }
        }
    }
}
