package org.smack.fx;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.smack.application.CliApplication;

/**
 * https://developer.android.com/guide/topics/graphics/vector-drawable-resources
 * @author MICBINZ
 *
 */
public class SvgParseAndroid
    extends CliApplication
{

    private class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> PREF_MAP =
                new HashMap<String,String>();

        public SimpleNamespaceContext() {
        }

        public SimpleNamespaceContext add( String prefix, String uri )
        {
            PREF_MAP.put( prefix, uri );
            return this;
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return PREF_MAP.get(prefix);
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

        SimpleNamespaceContext snspctx = new SimpleNamespaceContext();
        snspctx.add( "android", "http://schemas.android.com/apk/res/android" );

        xpath.setNamespaceContext( snspctx );

        XPathExpression expr = xpath.compile( expression );

        var x = expr.evaluate( doc, XPathConstants.STRING ).toString();
        String s = x;

        out( "Got: '%s'%n", s );
    }

    public static void main(String[] argv) {
        launch(SvgParseAndroid::new, argv);
    }
}
