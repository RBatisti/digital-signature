package utils;

import config.Config;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Cryptography {
    public static String generateHashPassword(String password, byte[] salt) {
        return Base64.getEncoder().encodeToString(SCrypt.generate(password.getBytes(), salt, Config.COST_FACTOR, Config.BLOCK_SIZE, Config.PARALLELIZATION_USER, Config.KEY_LENGTH));
    }

    public static byte[] generateKeyDecrypt(String password, byte[] salt) {
        return SCrypt.generate(password.getBytes(), salt, Config.COST_FACTOR, Config.BLOCK_SIZE, Config.PARALLELIZATION_KEY, Config.KEY_LENGTH);
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


    public static String generateFileHash(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            SHA256.Digest digest = new SHA256.Digest();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            fis.close();

            return Hex.toHexString(digest.digest());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateOriginHash(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            SHA256.Digest digest = new SHA256.Digest();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            fis.close();

            return Hex.toHexString(digest.digest());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateFileHash(byte[] content) {
        try {
            SHA256.Digest digest = new SHA256.Digest();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
            byte[] buffer = new byte[1024];

            int bytesRead;
            while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byteArrayInputStream.close();

            return Hex.toHexString(digest.digest());


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String sign(String hash, PrivateKey privateKey) {
        try {
            /*/
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            /*/

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(hash.getBytes());
            byte[] signedBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean verifySignature(String hash, byte[] timeBytes, String signatureBase64, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(hash.getBytes());
            signature.update(timeBytes);

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            return signature.verify(signatureBytes);

        } catch (Exception e) {
            return false;
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
