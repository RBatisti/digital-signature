package security;

import config.Config;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {
    public static String encrypt(byte[] key, String text) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES", new BouncyCastleProvider());

            IvParameterSpec ivSpec = generateIV();

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            byte[] encryptedWithIV = new byte[ivSpec.getIV().length + encrypted.length];

            System.arraycopy(ivSpec.getIV(), 0, encryptedWithIV, 0, ivSpec.getIV().length);
            System.arraycopy(encrypted, 0, encryptedWithIV, ivSpec.getIV().length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedWithIV);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(byte[] key, String encryptedText) {
        try {
            byte[] encriptedWithIV = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[16];

            System.arraycopy(encriptedWithIV, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            byte[] encriptedBytes = new byte[encriptedWithIV.length - iv.length];
            System.arraycopy(encriptedWithIV, iv.length, encriptedBytes, 0, encriptedBytes.length);

            SecretKey secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES", new BouncyCastleProvider());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(encriptedBytes);

            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateKeyDecrypt(String password, byte[] salt) {
        return SCrypt.generate(password.getBytes(), salt, Config.COST_FACTOR, Config.BLOCK_SIZE, Config.PARALLELIZATION_KEY, Config.KEY_LENGTH);
    }

    public static String generateHashPassword(String password, byte[] salt) {
        return Base64.getEncoder().encodeToString(SCrypt.generate(password.getBytes(), salt, Config.COST_FACTOR, Config.BLOCK_SIZE, Config.PARALLELIZATION_USER, Config.KEY_LENGTH));
    }

    public static KeyPair keyPairGenerator() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);

            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair loadKeyPair(String publicKeyStr, String privateKeyStr) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
