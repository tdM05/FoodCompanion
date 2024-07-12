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
        String privateKeyString = "-----BEGIN RSA PRIVATE KEY-----\n" + "MIICXQIBAAKBgQDKQtJAyCu5FHwDncK2LB/J5ClJhulGggyc7vwtji6TJHtSJfgD\n" + "4TLpHRIHh/cHqf3brhpQtYB9yjKlwogji/OzedY2mdTdSOP8O6suJYu3QENN2xG/\n" + "HvT8UiYK3feVLbJtukhJm7eSuwfMDsjHh4AK7g11fVs6EmY+foh3mjoKLQIDAQAB\n" + "AoGAR8N/wDaFtOx8t/fAv0xWlxaaQ5lXqYm5GfF9jlhVVCXsj5AjOJUtsCJ9ZCis\n" + "0I5TIR/b/Gj5xyf34nJsRViBxbnf6XdLGyXmzsNxWZoWbM70JaqU3iQKm605/EnD\n" + "vPgrI0AMfc/h6Kog0zLrKWKkna+wE5839yMmm7WPqgvxSc0CQQDoud5e3yZu/1e+\n" + "7piFZZl6StAecl+k10Wq5kzJeVQRffDB3JCca65H/W1EZIzEh76pUNr7SYAIIcbK\n" + "jzOdbj1vAkEA3n0AudM3mBzklLEUSHs1ZSqFkUMNP9MNIikwkZ/9Z2AlhW5gnwiv\n" + "dgeXonTqlTFux4e7uyKZoJpJcKAgmMicIwJBAIMl206TalE6y/Po+UKTUr470rSV\n" + "t5hpR/Va+wK+wMVqt3ZIGaZMeFZRVnYoQ7us06EO05iwftoWTrRvpqKdMTkCQBkE\n"
                + "QzWhy0l+TjFt69Luj6Vtb5FS0cWQbJSfvwdQzwR1qiJjs9eN+XSzC9jHfq0B3uvu\n" + "lixHirClSIayapfjTrMCQQCM8d97py4u9hCdCpsHBDt54dXkHsDA2abNzaPri/YA\n" + "pNFZGrfXKVGSLFOfsuf7Wj+yL7ew6ZVKOMYdJ+zb9Wwv\n" + "-----END RSA PRIVATE KEY-----";

        try{
            Reader privateKeyReader = new StringReader(privateKeyString);

            PEMParser privatePemParser = new PEMParser(privateKeyReader);
            Object privateObject = privatePemParser.readObject();
            PEMKeyPair pemKeyPair = (PEMKeyPair) privateObject;
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            PublicKey publicKey = converter.getPublicKey(pemKeyPair.getPublicKeyInfo());

            byte[] encrypted = encrypt(publicKey, message);
            return encrypted.toString();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    private static byte[] encrypt(Key pubkey, String text) {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA", "BC");
            rsa.init(Cipher.ENCRYPT_MODE, pubkey);
            return rsa.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
