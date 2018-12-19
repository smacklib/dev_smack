/* $Id$
 *
 * Unpublished work.
 * Copyright © 2018 Michael G. Binz
 */
package org.smack.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.jdesktop.util.ResourceManager;
import org.jdesktop.util.ServiceManager;

/**
 * Security utilities.
 *
 * @author Michael Binz
 */
public class SecurityUtil
{
    private final static String ALGORITHM =
            "SHA1withDSA";
    private final static String PROVIDER =
            "SUN";

    /**
     * Check if the passed data has a valid signature.
     *
     * @param pub The signature's public key.
     * @param sign The signature.
     * @param data The signed data.
     * @return True if the verification succeeded.
     */
    public static boolean performVerify(
            PublicKey pub,
            byte[] sign,
            byte[] data )
    {
        try
        {
            Signature signature =
                    Signature.getInstance(ALGORITHM, PROVIDER);

            signature.initVerify( pub );
            signature.update( data );

            return signature.verify( sign );
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    /**
     * Signs the passed data using the private key.
     *
     * @param priv The key to use.
     * @param data The data to sign.
     * @return The binary signature.
     * @throws Exception In case of an error.
     */
    public static byte[] performSign( PrivateKey priv, byte[] data ) throws Exception
    {
        Signature signature =
                Signature.getInstance(ALGORITHM, PROVIDER);

        signature.initSign(
                priv );
        signature.update(
                data );

        return signature.sign();
    }

    /**
     * Read the first certificate from the passed stream and close the stream.
     *
     * @param fis The certificate stream. This is closed after reading.
     * @return The certificate, never null. If no certificate is in the file,
     * an exception is thrown.
     * @see #writeCert(X509Certificate, File)
     * @throws Exception In case of an error.
     */
    public static X509Certificate readCert( InputStream fis )
        throws Exception
    {
        try ( BufferedInputStream bis = new BufferedInputStream( fis ) )
        {
            CertificateFactory cf =
                    CertificateFactory.getInstance("X.509");

            while (bis.available() > 0)
            {
                Certificate cert = cf.generateCertificate(bis);

                // We return the first certificate we find.
                // TODO: we could read more, but this is not needed now.
                return (X509Certificate)cert;
            }

            // File did not contain a certificate.
            throw new IllegalArgumentException();
        }
    }

    /**
     * Read the first certificate from the passed file.
     *
     * @param f The certificate file.
     * @return The certificate.
     * @see #writeCert(X509Certificate, File)
     * @throws Exception In case of an error.
     */
    public static X509Certificate readCert( File f ) throws Exception
    {
        return readCert( new FileInputStream( f ) );
    }

    /**
     * Write a certificate into the passed file.
     *
     * @param cert The certificate to write.
     * @param f The target file. If the file exists the content is
     * overwritten.
     * @see #readCert(File)
     * @throws Exception In case of an error.
     */
    public static void writeCert( X509Certificate cert, File f )
            throws Exception
    {
        try ( FileWriter sw = new FileWriter( f ) )
        {
            sw.write("-----BEGIN CERTIFICATE-----\n");
            sw.write(
                    Base64.getMimeEncoder().encodeToString(
                            cert.getEncoded() ) );
            sw.write("\n-----END CERTIFICATE-----\n");

            sw.flush();
        }
    }

    static
    {
        ServiceManager.getApplicationService( ResourceManager.class )
            .injectResources( SecurityUtil.class );
    }

    private SecurityUtil()
    {
        throw new AssertionError();
    }
}
