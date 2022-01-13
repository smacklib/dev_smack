package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.junit.Test;

/**
 * Create a keystore with a self-signed certificate:
 * <pre>
 * # Big time
 * keytool -genkey -alias server -keyalg RSA -keysize 2048 -sigalg SHA256withRSA -keystore keystore.pk12 -storepass lumumba -dname "CN=my.server.com, OU=EastCoast, O=MyComp Ltd, L=New York, ST=, C=US"   -ext "SAN=dns:my.server.com,dns:www.my.server.com,ip:11.22.33.44"   -validity 7200
 * # Simpler, using defaults.
 * keytool -genkey -alias smacktest -keystore smack.pk12 -dname "CN=smack, OU=github, O=smacklib, L=Berlin, ST=Berlin, C=DE" -validity 7200 -storepass smackit
 * </pre>
 *
 * List keystore contents:
 * <pre>
 * keytool -list -v -keystore smack.pk12 -storepass smackit
 * </pre>
 *
 * @author MICBINZ
 */
public class SecurityUtilTest
{
    private final static String PASSWORD = "smackit";
    private final static String ALIAS = "smacktest";
    private final static String KEYSTORE = "smack.pk12";

    @Test
    public void loadTest() throws Exception, NoSuchProviderException
    {
        KeyStore ks =
                KeyStore.getInstance( "pkcs12" );

        try ( InputStream ksfis = SecurityUtilTest.class.getResourceAsStream( KEYSTORE ) )
        {
            BufferedInputStream ksbufin =
                    new BufferedInputStream( ksfis );

            ks.load( ksbufin, PASSWORD.toCharArray() );
        }

        PrivateKey priv = (PrivateKey)ks.getKey(
                ALIAS,
                PASSWORD.toCharArray() );
        assertNotNull( priv );

        Certificate cert =
                ks.getCertificate(ALIAS);
        assertNotNull( cert );

        PublicKey pub =
                cert.getPublicKey();
        assertNotNull( pub );
    }

    @Test
    public void writeReadCert() throws Exception
    {
        KeyStore ks =
                KeyStore.getInstance( "pkcs12" );

        try ( InputStream ksfis = SecurityUtilTest.class.getResourceAsStream( KEYSTORE ) )
        {
            BufferedInputStream ksbufin =
                    new BufferedInputStream( ksfis );

            ks.load( ksbufin, PASSWORD.toCharArray() );
        }

        X509Certificate cert =
                (X509Certificate)ks.getCertificate(ALIAS);
        assertNotNull( cert );

        cert.checkValidity();
        var serialNumber = cert.getSerialNumber();

        File tf = File.createTempFile( getClass().getSimpleName(), null );
        SecurityUtil.writeCert( cert, tf );

        X509Certificate readBack = SecurityUtil.readCert( tf );

        assertEquals( serialNumber, readBack.getSerialNumber() );

        FileUtil.delete( tf );
    }

    @Test
    public void readAndCheckCert() throws Exception
    {
        KeyStore ks =
                KeyStore.getInstance( "pkcs12" );

        try ( InputStream ksfis = SecurityUtilTest.class.getResourceAsStream( KEYSTORE ) )
        {
            BufferedInputStream ksbufin =
                    new BufferedInputStream( ksfis );

            ks.load( ksbufin, PASSWORD.toCharArray() );
        }

        X509Certificate expectedCert =
                (X509Certificate)ks.getCertificate(ALIAS);
        assertNotNull( expectedCert );

        var certFromResources = SecurityUtil.readCert(
                SecurityUtilTest.class.getResourceAsStream( "smackPublic.cer" ) );

        assertEquals(
                expectedCert.getSerialNumber(),
                certFromResources.getSerialNumber() );
    }

    @Test
    public void encryptDecryptTest() throws Exception, NoSuchProviderException
    {
        // https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
        KeyPairGenerator keyGen =
                KeyPairGenerator.getInstance( "RSA" );
        SecureRandom random =
                SecureRandom.getInstanceStrong();

        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        String geheimnis = "Donald trinkt Gänsewein!";

        byte[] encrypted = SecurityUtil.encrypt(
                geheimnis.getBytes(),
                priv );

        byte [] decrypted = SecurityUtil.decrypt(
                encrypted,
                pub );

        String result = new String( decrypted );

        assertEquals( geheimnis, result );
    }
}
