/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2019 Michael G. Binz
 */
package org.smack.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * XML utility operations.
 *
 * @author Michael G. Binz
 */
public class XmlUtil
{
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
        return transform(
                new StreamSource( xslt ),
                new InputSource( toTransform ) );
    }

    /**
     * Transform a file based on an XSLT transformation. Access to
     * non-existent dtds is ignored.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform( Reader xslt, Reader toTransform )
            throws Exception
    {
        return transform(
                new StreamSource( xslt ),
                new InputSource( toTransform ) );
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
        try ( Reader xsltReader = new FileReader( xslt ) )
        {
            try ( Reader toTransformReader = new FileReader( toTransform ) )
            {
                StreamSource xsltSource =
                        new StreamSource( xsltReader );
                xsltSource.setSystemId(
                        xslt );

                return transform(
                        xsltSource,
                        new InputSource( toTransformReader ) );
            }
        }
    }

    /**
     * Transform a file based on an XSLT transformation. Access to
     * non-existent dtds is ignored.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    private static String transform( StreamSource xslt, InputSource toTransform )
            throws Exception
    {
        XMLReader reader =
                SAXParserFactory.newInstance().newSAXParser().
                getXMLReader();

        // Set a resolver that ignores access to non-existent dtds.
        reader.setEntityResolver(new EntityResolver()
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
        });

        TransformerFactory tFactory =
                TransformerFactory.newInstance();
        Transformer transformer =
                tFactory.newTransformer( xslt );
        ByteArrayOutputStream result =
                new ByteArrayOutputStream();
        transformer.transform(
                new SAXSource(
                        reader,
                        toTransform),
                new StreamResult( result ) );

        return result.toString();
    }

    public static String transform8( File stylesheet, File datafile )
            throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();


        DocumentBuilder builder = factory.newDocumentBuilder();

        // Set a resolver that ignores access to non-existent dtds.
        builder.setEntityResolver(new EntityResolver()
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
        });

        document = builder.parse(datafile);

        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);
        stylesource.setSystemId( stylesheet.getPath() );
        Transformer transformer = tFactory.newTransformer(stylesource);


        DOMSource source = new DOMSource(document);

        StreamResult result = new StreamResult(bos);
        transformer.transform(source, result);


        return bos.toString();
    }

    private XmlUtil()
    {
        throw new AssertionError();
    }
}
