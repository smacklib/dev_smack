package org.smack.fx;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.smack.application.CliApplication;
import org.w3c.dom.NodeList;

/**
 * https://developer.android.com/guide/topics/graphics/vector-drawable-resources
 * @author MICBINZ
 *
 */
public class SvgParseAndroid
extends CliApplication
{
    private static class NamespaceContextImpl
    // Deliberate inheritance to inherit the normal iteration operations and a
    // ordentlichen toString().
    extends HashMap<String, String>
    implements NamespaceContext
    {
        Map<String, String> m;

        public NamespaceContextImpl() {
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return get(prefix);
        }

        @Override
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }

    public static String getXPath(
            File xmlDocument,
            String expression
            ) throws Exception
    {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );

        DocumentBuilder builder =
                factory.newDocumentBuilder();
        org.w3c.dom.Document doc =
                builder.parse( xmlDocument );
        XPathFactory xPathfactory =
                XPathFactory.newInstance();
        XPath xpath =
                xPathfactory.newXPath();

        xpath.setNamespaceContext(
                getNamespaces( xmlDocument ) );

        XPathExpression expr =
                xpath.compile( expression );

        return expr.evaluate( doc, XPathConstants.STRING ).toString();
    }

    public static double getXPathAsDouble(
            File xmlDocument,
            String expression
            ) throws Exception
    {
        return Double.parseDouble(
                getXPath( xmlDocument, expression ) );
    }

    public static List<String> getXPath(
            File xmlDocument,
            String ... expressions
            ) throws Exception
    {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );

        DocumentBuilder builder =
                factory.newDocumentBuilder();
        org.w3c.dom.Document doc =
                builder.parse( xmlDocument );
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
                    expr.evaluate( doc, XPathConstants.STRING ).toString() );
        }

        return result;
    }

    public static NamespaceContextImpl getNamespaces( File xmlDocument ) throws Exception
    {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        // Needs to be true for getLocalName() below to return meaningful data.
        factory.setNamespaceAware( true );

        DocumentBuilder builder =
                factory.newDocumentBuilder();
        org.w3c.dom.Document doc =
                builder.parse( xmlDocument );
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

    @Command
    private void xpath( File f, String expression )
            throws Exception
    {
        err( "Expression: '%s'%n", expression );

        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        out( "Namespace aware: %s%n",
                factory.isNamespaceAware() );

        DocumentBuilder builder =
                factory.newDocumentBuilder();
        org.w3c.dom.Document doc =
                builder.parse( f );
        XPathFactory xPathfactory =
                XPathFactory.newInstance();
        XPath xpath =
                xPathfactory.newXPath();

        xpath.setNamespaceContext(
                getNamespaces( f ) );

        XPathExpression expr = xpath.compile( expression );

        var x = expr.evaluate( doc, XPathConstants.STRING ).toString();
        String s = x;

        out( "Got: '%s'%n", s );
    }

    @Command
    private void xpath( File f, String expression, String x2 )
            throws Exception
    {
        err( "Expressions: '%s', '%s'%n", expression, x2 );

        var results = getXPath( f, expression, x2 );

        out( "Got: '%s'%n", results.get( 0 ) );
        out( "Got: '%s'%n", results.get( 1 ) );
    }

    @Command
    private void ns( File f )
            throws Exception
    {
        var nsContext =
                getNamespaces( f );

        out( "Found %d nodes.%n", nsContext.size() );
        nsContext.forEach(
                (k,v) ->
                out( "%s=\"%s\"%n", k, v ) );
    }

    public static void main(String[] argv) {
        launch(SvgParseAndroid::new, argv);
    }
}
