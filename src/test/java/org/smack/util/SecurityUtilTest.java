package org.smack.util;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.junit.Test;

public class SecurityUtilTest
{
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
