/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2019 Michael G. Binz
 */
package org.smack.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.smack.util.Disposer;
import org.smack.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XML utility operations.
 *
 * @author Michael G. Binz
 */
public class XmlUtil
{
    /**
     * A resolver that ignores access to non-existent dtds.
     */
    private static EntityResolver  EMPTY_DTD_RESOLVER = new EntityResolver()
    {
        @Override
        public InputSource resolveEntity(
                String publicId,
                String systemId)
                        throws SAXException, IOException
        {
            if (systemId.endsWith(".dtd"))
                return new InputSource(new StringReader(" "));

            return null;
        }
    };

    /**
     * Transform a file based on an XSLT transformation. Access to
     * non-existent dtds is ignored.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform( InputStream xslt, InputStream toTransform )
            throws Exception
    {
        return transformImpl(
                xslt,
                toTransform,
                null,
                Collections.emptyMap() );
    }

    /**
     * Transform a file based on an XSLT transformation. Access to
     * non-existent dtds is ignored.  This version of the operation
     * internally sets the systemId of the xslt, so that access on the
     * stylesheet via the xls 'document( '' )' operation works.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform( File xslt, File toTransform )
            throws Exception
    {
        return transform(
                xslt,
                toTransform,
                Collections.emptyMap() );
    }

    /**
     * Transform a file based on an XSLT transformation. Access to
     * non-existent dtds is ignored.  This version of the operation
     * internally sets the systemId of the xslt, so that access on the
     * stylesheet via the xls 'document( '' )' operation works.
     *
     * @param stylesheet The transformation.
     * @param datafile The file to transform.
     * @param parameters Parameters for the stylesheet.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform(
            File stylesheet,
            File datafile,
            Map<String,Object> parameters )
                    throws Exception
    {
        return transformImpl(
                new FileInputStream( stylesheet ),
                new FileInputStream( datafile ),
                stylesheet.getPath(),
                parameters );

    }

    /**
     * This operation works on Java 8.  TODO(micbinz) integrate.
     *
     * @param stylesheet The stylesheet.
     * @param datafile The input to process.
     * @param parameters Parameters to be passed to the stylesheet.
     * @return The processing result.
     * @throws Exception In case of an error.
     */
    public static String transform(
            InputStream stylesheet,
            InputStream datafile,
            Map<String,Object> parameters )
                    throws Exception
    {
        return transformImpl(
                stylesheet,
                datafile,
                null,
                parameters );
    }

    /**
     * Internal implementation of transform including resource management.
     * The passed input streams get closed after processing.
     *
     * @param stylesheet The stylesheet.
     * @param datafile The input to process.
     * @param systemId An optional system id.  Pass null if not needed.
     * @param parameters Parameters to be passed to the stylesheet.
     * @return The processing result.
     * @throws Exception In case of an error.
     */
    private static String transformImpl(
            InputStream stylesheet,
            InputStream datafile,
            String systemId,
            Map<String,Object> parameters )
                    throws Exception
    {
        try ( Disposer d = new Disposer() )
        {
            d.register( stylesheet );
            d.register( datafile );

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();
            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            builder.setEntityResolver(
                    EMPTY_DTD_RESOLVER );
            Document document =
                    builder.parse(datafile);
            TransformerFactory tFactory =
                    TransformerFactory.newInstance();
            StreamSource stylesource =
                    new StreamSource(stylesheet);

            if ( StringUtil.hasContent( systemId ) )
                stylesource.setSystemId(
                        systemId );

            Transformer transformer =
                    tFactory.newTransformer(stylesource);

            parameters.forEach(
                    (k,v) -> transformer.setParameter( k, v ) );

            DOMSource source =
                    new DOMSource(document);
            StreamResult result =
                    new StreamResult(bos);
            transformer.transform(
                    source,
                    result);

            return bos.toString();
        }
    }

    /**
     * Evaluate an xpath against an XML-document.
     *
     * @param xmlDocument The document.
     * @param expression The xpath.
     * @return The result of the expression.  The empty string if the
     * expression did not select data.
     * @throws Exception In case of an error.  Note that it is no error
     * if the xpath does not select data.
     */
    public static String getXPath(
            InputStream xmlDocument,
            String expression )
                    throws Exception
    {
        return getXPath(
                xmlDocument,
                new String[] { expression } )
                .get( 0 );
    }

    /**
     * Evaluate an xpath against an XML-document.
     *
     * @param xmlDocument The document.
     * @param expression The xpath.
     * @return The result of the expression.  The empty string if the
     * expression did not select data.
     * @throws Exception In case of an error.  Note that it is no error
     * if the xpath does not select data.
     */
    public static List<String> getXPathNodes(
            InputStream xmlDocument,
            String expression )
                    throws Exception
    {
        return getXPathNodes(
                xmlDocument,
                new String[] { expression } )
                .get( 0 );
    }

    /**
     * Evaluate a set of xpath-expressions against an XML-document.
     *
     * @param xmlDocument The document.
     * @param expressions The xpath.
     * @return The result of the expressions in a list.
     * @throws Exception In case of an error.
     * @see #getXPath(File, String)
     */
    private static List<String> getXPath(
            Document xmlDocument,
            String ... expressions )
                    throws Exception
    {
        XPathFactory xPathfactory =
                XPathFactory.newInstance();
        XPath xpath =
                xPathfactory.newXPath();

        xpath.setNamespaceContext(
                getNamespaces( xmlDocument ) );

        var result =
                new ArrayList<String>();

        for ( String c : expressions )
        {
            XPathExpression expr =
                    xpath.compile( c );

            result.add(
                    expr.evaluate( xmlDocument, XPathConstants.STRING ).toString() );
        }

        return result;
    }

    /**
     * Evaluate a set of xpath-expressions against an XML-document.
     *
     * @param xmlDocument The document.
     * @param expressions The xpath.
     * @return The result of the expressions in a list.
     * @throws Exception In case of an error.
     * @see #getXPath(File, String)
     */
    private static List<List<String>> getXPathNodes(
            Document xmlDocument,
            String ... expressions )
                    throws Exception
    {
        XPathFactory xPathfactory =
                XPathFactory.newInstance();
        XPath xpath =
                xPathfactory.newXPath();

        xpath.setNamespaceContext(
                getNamespaces( xmlDocument ) );

        var result =
                new ArrayList<List<String>>();

        for ( String c : expressions )
        {
            XPathExpression expr =
                    xpath.compile( c );

            NodeList x =
                    (NodeList)expr.evaluate( xmlDocument, XPathConstants.NODESET );

            var list = new ArrayList<String>( x.getLength() );

            for ( int i = 0 ; i < x.getLength() ; i++ )
                list.add( x.item( i ).getTextContent()  );

            result.add( list );
        }

        return result;
    }

    public static List<String> getXPath(
            InputStream xmlDocument,
            String ... expressions
            ) throws Exception
    {
        try ( xmlDocument )
        {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );

            DocumentBuilder builder =
                    factory.newDocumentBuilder();
            Document doc =
                    builder.parse( xmlDocument );

            return getXPath( doc, expressions );
        }
    }

    public static List<List<String>> getXPathNodes(
            InputStream xmlDocument,
            String ... expressions
            ) throws Exception
    {
        try ( xmlDocument )
        {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );

            DocumentBuilder builder =
                    factory.newDocumentBuilder();
            Document doc =
                    builder.parse( xmlDocument );

            return getXPathNodes( doc, expressions );
        }
    }

    public static <R> R getXPathAs(
            Function<String, R> converter,
            InputStream xmlDocument,
            String xpath )
                    throws Exception
    {
        return converter.apply(
                getXPath( xmlDocument, xpath ) );
    }

    public static <R> List<R> getXPathAs(
            Function<String, R> converter,
            InputStream xmlDocument,
            String ... expressions )
                    throws Exception
    {
        List<String> strings = getXPath(
                xmlDocument,
                expressions );
        List<R> result =
                strings.stream().map( converter ).collect(
                        Collectors.toList() );
        return result;
    }

    private static NamespaceContextImpl getNamespaces( Document doc ) throws Exception
    {
        XPathFactory xPathfactory =
                XPathFactory.newInstance();
        XPath xpath =
                xPathfactory.newXPath();

        XPathExpression expr = xpath.compile( "//namespace::*" );

        NodeList x = (NodeList)expr.evaluate( doc, XPathConstants.NODESET );

        NamespaceContextImpl result =
                new NamespaceContextImpl();

        for ( int i = 0 ; i < x.getLength() ; i++ )
        {
            var q =
                    x.item( i );
            result.put(
                    q.getLocalName(),
                    q.getNodeValue() );
        }

        return result;
    }

    private XmlUtil()
    {
        throw new AssertionError();
    }
}
