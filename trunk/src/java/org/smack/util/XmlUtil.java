/**
 * $Id$
 *
 * Unpublished work.
 * Copyright © 2018-19 Michael G. Binz
 */
package org.smack.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XML utility operations.
 *
 * @version $Rev$
 * @author Michael Binz
 */
public class XmlUtil
{
    /**
     * Transform a file based on an XSLT transformation.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform( InputStream xslt, Reader toTransform )
            throws Exception
    {
        TransformerFactory  tFactory =
                TransformerFactory.newInstance();

        Source xslSource = new
            StreamSource( xslt );
        Transformer transformer =
            tFactory.newTransformer( xslSource );

        ByteArrayOutputStream result =
                new ByteArrayOutputStream();
        transformer.transform(
                new StreamSource( toTransform ),
                new StreamResult( result ) );

        return result.toString();
    }

    /**
     * Transform a file based on an XSLT transformation.
     *
     * @param xslt The transformation.
     * @param toTransform The file to transform.
     * @return The result of the transformation.
     * @throws Exception In case of an error.
     */
    public static String transform( Reader xslt, Reader toTransform )
            throws Exception
    {
        TransformerFactory  tFactory =
                TransformerFactory.newInstance();

        Source xslSource = new
            StreamSource( xslt );
        Transformer transformer =
            tFactory.newTransformer( xslSource );

        ByteArrayOutputStream result =
                new ByteArrayOutputStream();
        transformer.transform(
                new StreamSource( toTransform ),
                new StreamResult( result ) );

        return result.toString();
    }

    private XmlUtil()
    {
        throw new AssertionError();
    }
}
