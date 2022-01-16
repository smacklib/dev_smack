package org.smack.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.junit.Test;
import org.smack.util.SecurityUtil.DecryptionFailed;

/**
 * Create a keystore with a self-signed certificate:
 * <pre>
 * # Big time
 * keytool -genkey -alias clever -keyalg RSA -keysize 2048 -keystore smack.pk12 -dname "CN=clever, OU=github, O=smacklib, L=Berlin, ST=Berlin, C=DE" -validity 7200 -storepass smackit
 * keytool -genkey -alias smart -keyalg RSA -keysize 2048 -keystore smack.pk12 -dname "CN=smart, OU=github, O=smacklib, L=Berlin, ST=Berlin, C=DE" -validity 7200 -storepass smackit
 * </pre>
 *
 * Note that when DSA -- which is the default -- is used, the encryption
 * and decryption tests fail.
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

    private final static String ALIAS_1 = "clever";
    private final static String ALIAS_2 = "smart";

    private final static String KEYSTORE = "smack.pk12";

    private final static String ThePayload = "Donald trinkt Gänsewein!";

    private KeyStore getKeystore() throws Exception
    {
        KeyStore result =
                KeyStore.getInstance( "pkcs12" );

        try ( InputStream ksfis = SecurityUtilTest.class.getResourceAsStream(
                KEYSTORE ) )
        {
            result.load( ksfis, PASSWORD.toCharArray() );
        }

        return result;
    }

    @Test
    public void loadTest() throws Exception
    {
        KeyStore ks = getKeystore();

        PrivateKey priv_1 = (PrivateKey)ks.getKey(
                ALIAS_1,
                PASSWORD.toCharArray() );
        assertNotNull( priv_1 );

        PrivateKey priv_2 = (PrivateKey)ks.getKey(
                ALIAS_2,
                PASSWORD.toCharArray() );
        assertNotNull( priv_2 );

        Certificate cert_1 =
                ks.getCertificate(ALIAS_1);
        assertNotNull( cert_1 );
        assertNotNull( cert_1.getPublicKey() );

        Certificate cert_2 =
                ks.getCertificate(ALIAS_1);
        assertNotNull( cert_2 );
        assertNotNull( cert_2.getPublicKey() );
    }

    @Test
    public void writeReadCert() throws Exception
    {
        KeyStore ks = getKeystore();

        X509Certificate cert =
                (X509Certificate)ks.getCertificate(ALIAS_1);
        assertNotNull( cert );

        File tf = File.createTempFile( getClass().getSimpleName(), null );
        SecurityUtil.writeCert( cert, tf );

        X509Certificate readBack = SecurityUtil.readCert( tf );

        assertEquals(
                cert.getSerialNumber(),
                readBack.getSerialNumber() );

        FileUtil.delete( tf );
    }

    @Test
    public void readAndCheckCert() throws Exception
    {
        KeyStore ks = getKeystore();

        X509Certificate expectedCert =
                (X509Certificate)ks.getCertificate(ALIAS_1);
        assertNotNull( expectedCert );

        var certFromResources = SecurityUtil.readCert(
                SecurityUtilTest.class.getResourceAsStream( "smackPublic.cer" ) );

        assertEquals(
                expectedCert.getSerialNumber(),
                certFromResources.getSerialNumber() );
    }

    @Test
    public void encryptDecryptTest() throws Exception
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

        byte[] encrypted = SecurityUtil.encrypt(
                priv,
                ThePayload.getBytes() );

        byte [] decrypted = SecurityUtil.decrypt(
                pub,
                encrypted );

        String result = new String( decrypted );

        assertEquals( ThePayload, result );
    }

    @Test
    public void encryptDecryptTestFromKeystore() throws Exception
    {
        KeyStore ks = getKeystore();

        PrivateKey priv = (PrivateKey)ks.getKey(
                ALIAS_1,
                PASSWORD.toCharArray() );

        PublicKey pub =  JavaUtil.makex( () -> {
            var c = ks.getCertificate( ALIAS_1 );
            assertNotNull( c );
            return c.getPublicKey();
        } );
        assertNotNull( pub );

        byte[] encrypted = SecurityUtil.encrypt(
                priv,
                ThePayload.getBytes() );

        byte [] decrypted = SecurityUtil.decrypt(
                pub,
                encrypted );

        String result = new String( decrypted );

        assertEquals( ThePayload, result );
    }

    @Test( expected = DecryptionFailed.class )
    public void encryptDecryptTestFromKeystoreFail() throws Exception
    {
        KeyStore ks = getKeystore();

        // We take the private key 1.
        PrivateKey priv = (PrivateKey)ks.getKey(
                ALIAS_1,
                PASSWORD.toCharArray() );
        assertNotNull( priv );

        byte[] encrypted = SecurityUtil.encrypt(
                priv,
                ThePayload.getBytes() );

        // ... and try to decrypt with public key 2.
        PublicKey pub =  JavaUtil.makex( () -> {
            var c = ks.getCertificate( ALIAS_2 );
            assertNotNull( c );
            return c.getPublicKey();
        } );
        assertNotNull( pub );

        // This must fail.
        SecurityUtil.decrypt(
                pub,
                encrypted );
        fail();
    }

    @Test
    public void signVerifyTest() throws Exception
    {
        // https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
        KeyPairGenerator keyGen =
                KeyPairGenerator.getInstance( SecurityUtil.SIGN_ALGORITHM );
        SecureRandom random =
                SecureRandom.getInstanceStrong();

        keyGen.initialize(1024, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        byte[] signature = SecurityUtil.sign(
                priv,
                ThePayload.getBytes() );

        var success = SecurityUtil.verifySignature(
                pub,
                ThePayload.getBytes(),
                signature );

        assertTrue( success );
    }

    @Test
    public void signVerifyTest2048() throws Exception
    {
        // https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
        KeyPairGenerator keyGen =
                KeyPairGenerator.getInstance( SecurityUtil.SIGN_ALGORITHM );
        SecureRandom random =
                SecureRandom.getInstanceStrong();

        keyGen.initialize(2048, random);

        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();

        byte[] signature = SecurityUtil.sign(
                priv,
                ThePayload.getBytes() );

        var success = SecurityUtil.verifySignature(
                pub,
                ThePayload.getBytes(),
                signature );

        assertTrue( success );
    }

    @Test
    public void signVerifyTestFromKeystore() throws Exception
    {
        KeyStore ks = getKeystore();

        PrivateKey priv = (PrivateKey)ks.getKey(
                ALIAS_1,
                PASSWORD.toCharArray() );

        PublicKey pub =  JavaUtil.makex( () -> {
            var c = ks.getCertificate( ALIAS_1 );
            assertNotNull( c );
            return c.getPublicKey();
        } );
        assertNotNull( pub );

        byte[] signature = SecurityUtil.sign(
                priv,
                ThePayload.getBytes() );

        var success = SecurityUtil.verifySignature(
                pub,
                ThePayload.getBytes(),
                signature );

        assertTrue( success );
    }

    @Test
    public void signVerifyTestFromKeystoreFail() throws Exception
    {
        KeyStore ks = getKeystore();

        PrivateKey priv = (PrivateKey)ks.getKey(
                ALIAS_1,
                PASSWORD.toCharArray() );

        PublicKey pub =  JavaUtil.makex( () -> {
            var c = ks.getCertificate( ALIAS_2 );
            assertNotNull( c );
            return c.getPublicKey();
        } );
        assertNotNull( pub );

        byte[] signature = SecurityUtil.sign(
                priv,
                ThePayload.getBytes() );

        var success = SecurityUtil.verifySignature(
                pub,
                ThePayload.getBytes(),
                signature );

        assertFalse( success );
    }
}
