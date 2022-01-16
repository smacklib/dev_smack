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
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;

/**
 * Security utilities.
 *
 * @author Michael Binz
 */
public class SecurityUtil
{
    /**
     * The algorithm used by the sign/verify operations.
     */
    public static final String SIGN_ALGORITHM = "RSA";

    private final static String ALGORITHM =
            "SHA1withRSA";
    private final static String PROVIDER =
            "SunRsaSign";

    /**
     * Check if the passed data has a valid signature.
     *
     * @param pub The signature's public key.
     * @param sign The signature.
     * @param data The signed data.
     * @return True if the verification succeeded.
     */
    public static boolean verifySignature(
            PublicKey pub,
            byte[] data,
            byte[] signature )
    {
        Signature sig = JavaUtil.make( () -> {
            try
            {
                return Signature.getInstance(ALGORITHM, PROVIDER);
            }
            catch (Exception e) {
                throw new RuntimeException( e );
            }
        });

        try
        {
            sig.initVerify( pub );
            sig.update( data );

            return sig.verify( signature );
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
     * @see #verifySignature(PublicKey, byte[], byte[])
     */
    public static byte[] sign(
            PrivateKey priv,
            byte[] data ) throws Exception
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
        Objects.requireNonNull( fis, "stream is null." );
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
        Objects.requireNonNull( f, "file is null." );
        return readCert( new FileInputStream( f ) );
    }

    /**
     * Read the first certificate from the passed URL.
     *
     * @param url The certificate URL.
     * @return The certificate.
     * @throws Exception In case of an error.
     */
    public static X509Certificate readCert( URL url ) throws Exception
    {
        Objects.requireNonNull( url, "url is null." );
        return readCert( url.openStream() );
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

    /**
     * The transformation used by the encrypt/decrypt operations.
     */
    public static final String CIPHER_ALGORITHM = "RSA";

    private static Cipher getCipher( String alg )
    {
        try {
            return Cipher.getInstance( CIPHER_ALGORITHM );
        }
        catch ( Exception e )
        {
            // This is an implementation or config error.  Convert
            // to runtime exceptions.
            throw new RuntimeException( "No Cipher for: " + CIPHER_ALGORITHM, e );
        }
    }

    /**
     * Encrypt the passed data.
     *
     * @param data The payload to encrypt.
     * @param key The encryption key.
     * @return The encrypted data.
     *
     * @throws InvalidKeyException
     * @see #decrypt(byte[], PublicKey)
     */
    public static byte[] encrypt( PrivateKey key, byte[] data )
        throws
            InvalidKeyException
    {
        Objects.requireNonNull( data );
        Objects.requireNonNull( key );

        Cipher cipher = getCipher( CIPHER_ALGORITHM );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                key );

        try
        {
            return cipher.doFinal( data );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * An exception thrown if decryption failed.
     */
    public static class DecryptionFailed extends Exception {
        private static final long serialVersionUID = 7569124449541888813L;

        DecryptionFailed( Exception cause )
        {
            super( cause );
        }
    }

    /**
     * Decrypt the passed data.
     *
     * @param encryptedData The data to decrypt.
     * @param key The key used for decryption.
     * @return The decrypted data.
     * @throws InvalidKeyException If the passed key is invalid.
     * @throws DecryptionFailed If the decryption step failed.
     */
    public static byte[] decrypt( PublicKey key, byte[] encryptedData )
            throws
                InvalidKeyException,
                DecryptionFailed
    {
        Cipher cipher = getCipher( CIPHER_ALGORITHM );

        cipher.init(
                Cipher.DECRYPT_MODE, Objects.requireNonNull( key ) );

        try {
            return cipher.doFinal( encryptedData );
        }
        catch ( Exception e )
        {
            // Convert the technical exceptions to a simpler exception
            // that signals that the decryption failed.
            throw new DecryptionFailed( e );
        }
    }

    private SecurityUtil()
    {
        throw new AssertionError();
    }
}
