package uicommunicator;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.PublicKey;
import java.security.Security;

import javax.crypto.Cipher;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Base64;


public class Encryptor {

    public static String RSAEncrypt(String message, String publicKeyString)
    {

        try{
            Reader publicKeyReader = new StringReader(publicKeyString);
            PEMParser publicPemParser = new PEMParser(publicKeyReader);
            Object publicObject = publicPemParser.readObject();

            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) publicObject;
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
            Cipher rsa;
            rsa = Cipher.getInstance("RSA", "BC");
            rsa.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = rsa.doFinal(message.getBytes());
            return encrypted.toString();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }


}
