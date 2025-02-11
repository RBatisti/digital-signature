package utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class SignFile {
    /*public static void signWord(File file, String sign, PrivateKey privateKey) {
        FileInputStream fis = new FileInputStream(file)
    }*/

    public static void signWord(File docxFile, File signedFile, PrivateKey privateKey) throws Exception {
        FileInputStream fis = new FileInputStream(docxFile);
        XWPFDocument document = new XWPFDocument(fis);

        byte[] docBytes = Files.readAllBytes(docxFile.toPath());

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(docBytes);
        byte[] digitalSignature = signature.sign();

        document.getProperties().getCoreProperties().setKeywords(Base64.getEncoder().encodeToString(digitalSignature));

        FileOutputStream fos = new FileOutputStream(signedFile);
        document.write(fos);
        fos.close();
    }
}
