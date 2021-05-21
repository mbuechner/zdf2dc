/*
 * Copyright 2019-2021 Michael Büchner <m.buechner@dnb.de>, Deutsche Digitale Bibliothek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ddb.labs.zdf2dc.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Michael Büchner <m.buechner@dnb.de>
 */
public class Reformatter {

    // private static final Logger LOG = LoggerFactory.getLogger(Reformatter.class);
    private final DocumentBuilderFactory docFactory;
    private final DocumentBuilder docBuilder;
    private static final String XSL = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
            + "<!-- See https://stackoverflow.com/questions/4593326/xsl-how-to-remove-unused-namespaces-from-source-xml/4594626#4594626 -->\n"
            + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
            + "    <xsl:output indent=\"yes\" omit-xml-declaration=\"no\" standalone=\"yes\" version=\"1.0\"/>\n"
            + "    <xsl:strip-space elements=\"*\" />\n"
            + "    <xsl:template match=\"node() | @*\" priority=\"-2\">\n"
            + "        <xsl:copy>\n"
            + "            <xsl:apply-templates select=\"node() | @*\" />\n"
            + "        </xsl:copy>\n"
            + "    </xsl:template>\n"
            + "    <xsl:template match=\"*\">\n"
            + "        <xsl:element name=\"{name()}\" namespace=\"{namespace-uri()}\">\n"
            + "            <xsl:variable name=\"vtheElem\" select=\".\" />\n"
            + "            <xsl:for-each select=\"namespace::*\">\n"
            + "                <xsl:variable name=\"vPrefix\" select=\"name()\" />\n"
            + "                <xsl:if test=\"$vtheElem/descendant::*[(namespace-uri() = current() and substring-before(name(), ':') = $vPrefix) or @*[substring-before(name(), ':') = $vPrefix]]\">\n"
            + "                    <xsl:copy-of select=\".\" />\n"
            + "                </xsl:if>\n"
            + "            </xsl:for-each>\n"
            + "            <xsl:apply-templates select=\"node() | @*\" />\n"
            + "        </xsl:element>\n"
            + "    </xsl:template>\n"
            + "</xsl:stylesheet>";

    private final Transformer transformer;

    public Reformatter() throws TransformerConfigurationException, ParserConfigurationException {
        DocumentBuilderFactory.newInstance().setNamespaceAware(true);
        // final TransformerFactory fact = new net.sf.saxon.TransformerFactoryImpl();
        final TransformerFactory fact = TransformerFactory.newInstance();
        transformer = fact.newTransformer(new StreamSource(new StringReader(XSL)));
        docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
    }

    /**
     * Re-formatiert XML
     * <ol>
     * <li>Alle Namespace-Deklarationen im Root-Elemnt deklarieren.</li>
     * <li>Entfernen von allen unnötigen Namespace-Deklarationen.</li>
     * <li>XML-Einrücken, Formatieren und Aufhübschen.</li>
     * </ol>
     * @param xml
     * @return
     * @throws TransformerException
     * @throws SAXException
     * @throws IOException 
     */
    public String reformat(String xml) throws TransformerException, SAXException, IOException {
        transformer.reset();

        final Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
        final Node root = doc.getFirstChild();

        final Map<String, String> ns = new HashMap<>();
        findAllNamespaces(root, ns);

        // consolidate ns list
        for (Map.Entry<String, String> e : ns.entrySet()) {
            if (EdmNamespaces.getUriNs().containsKey(e.getKey())) {
                ns.put(e.getKey(), EdmNamespaces.getUriNs().get(e.getKey()));
            }
        }

        readChildNodes(root, root, ns);

        final DOMSource source = new DOMSource(doc);
        final StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Find all namespaces in a node and its children and put them in list.
     *
     * @param node
     * @param list
     */
    protected static void findAllNamespaces(Node node, Map<String, String> list) {
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                final String ns = currentNode.getNamespaceURI();
                final String prefix = currentNode.getPrefix();
                if (ns != null && prefix != null) {
                    list.put(ns, prefix); // add ns of all nodes
                }
                final NamedNodeMap nnm = currentNode.getAttributes();
                for (int j = 0; j < nnm.getLength(); ++j) {
                    // list.put(nnm.item(j).getNamespaceURI(), nnm.item(j).getPrefix()); // add ns of all attr
                    final String attrNs = nnm.item(j).getNamespaceURI();
                    final String attrPrefix = EdmNamespaces.getUriNs().get(nnm.item(j).getNamespaceURI());
                    if (attrNs != null && attrPrefix != null) {
                        list.put(attrNs, attrPrefix);
                    }
                }
                findAllNamespaces(currentNode, list);
            }
        }
    }

    /**
     * Correct namespace prefixes by given list
     *
     * @param node
     * @param root
     * @param ns
     */
    protected static void readChildNodes(Node node, Node root, Map<String, String> ns) {

        if (ns.containsKey(node.getNamespaceURI())) {
            final String prefix = ns.get(node.getNamespaceURI());
            node.setPrefix(prefix);
            ((Element) root).setAttribute("xmlns:" + prefix, node.getNamespaceURI());
        }

        final NamedNodeMap nnm = node.getAttributes();
        if (nnm != null) {
            for (int i = 0; i < nnm.getLength(); ++i) {
                final Node attribute = nnm.item(i);
                if (ns.containsKey(attribute.getNamespaceURI())) {
                    final String prefix = ns.get(attribute.getNamespaceURI());
                    attribute.setPrefix(prefix);
                    ((Element) root).setAttribute("xmlns:" + prefix, attribute.getNamespaceURI());
                }
            }
        }
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                readChildNodes(currentNode, root, ns);
            }
        }

    }
}
