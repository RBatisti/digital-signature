package service;

import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.util.encoders.Hex;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import static config.Config.SIGNATURE_MARK;
import static config.Config.TIME_MARK;
import static utils.FileUtils.getOriginalFile;

public class SignatureService {
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

    public static byte[] signFile(File inputFile, PrivateKey privateKey) {
        try {
            byte[] fileContent = Files.readAllBytes(inputFile.toPath());

            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            byte[] timeBytes = time.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(generateFileHash(getOriginalFile(inputFile)).getBytes());
            signature.update(timeBytes);
            byte[] digitalSignature = signature.sign();

            byte[] signatureBase64 = Base64.getEncoder().encode(digitalSignature);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(fileContent);
            outputStream.write(TIME_MARK);
            outputStream.write(timeBytes);
            outputStream.write(SIGNATURE_MARK);
            outputStream.write(signatureBase64);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Hash file with SHA-256
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
}
