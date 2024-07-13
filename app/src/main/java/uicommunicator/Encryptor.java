package uicommunicator;

import java.io.Reader;
import java.io.StringReader;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;


public class Encryptor {

    public static byte[] RSAEncrypt(String message, String publicKeyString)
    {

        try{
            Reader publicKeyReader = new StringReader(publicKeyString);
            PEMParser publicPemParser = new PEMParser(publicKeyReader);
            Object publicObject = publicPemParser.readObject();

            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) publicObject;
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PublicKey publicKey = converter.getPublicKey(subjectPublicKeyInfo);
            Cipher rsa;
            rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, publicKey);
//            byte[] encrypted = rsa.doFinal(message.getBytes());
//            return Arrays.toString(encrypted);
            return rsa.doFinal(message.getBytes());
        }
        catch (Exception e)
        {
//            return e.getMessage();
            return new byte [0];
        }

    }


}
