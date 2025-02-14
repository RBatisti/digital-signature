package utils;

import com.itextpdf.io.font.otf.GsubLookupType1;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.SerializeOptions;
import model.User;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import session.SessionManager;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.*;


public class FileSigner {

    private static final byte[] SIGNATURE_MARK = "###SIGNATURE###".getBytes();
    private static final byte[] TIME_MARK = "###DATE###".getBytes();

    public static byte[] signFile(File inputFile, PrivateKey privateKey) {
        try {
            // Lê o conteúdo do arquivo original
            byte[] fileContent = Files.readAllBytes(inputFile.toPath());

            // Define a assinatura
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            byte[] timeBytes = time.getBytes();

            // Gera a assinatura digital
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(Cryptography.generateFileHash(FileSigner.getOriginalFile(inputFile)).getBytes());
            signature.update(timeBytes);
            byte[] digitalSignature = signature.sign();

            // Codifica a assinatura em Base64
            byte[] signatureBase64 = Base64.getEncoder().encode(digitalSignature);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(fileContent);
            outputStream.write(TIME_MARK);
            outputStream.write(timeBytes);
            outputStream.write(SIGNATURE_MARK);
            outputStream.write(signatureBase64);

            return outputStream.toByteArray();

            /*
            // exportar
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                fos.write(fileContent);
                fos.write(TIME_MARK);
                fos.write(timeBytes);
                fos.write(SIGNATURE_MARK);
                fos.write(signatureBase64);


                byte[] teste1 = Base64.getDecoder().decode(signatureBase64);
                System.out.println(Base64.getEncoder().encodeToString(teste1));

                System.out.println(Base64.getEncoder().encodeToString(digitalSignature));
            }
            */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void exportFile(byte[] fileBytes, String outputPath) {
        try {
            FileOutputStream fos = new FileOutputStream(outputPath);
            fos.write(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> getSignatures(File signedFile) {
        ArrayList<String> signatures = new ArrayList<>();
        try {
            byte[] fileContent = Files.readAllBytes(signedFile.toPath());
            if (findMarker(fileContent, SIGNATURE_MARK) == -1) {
                return signatures;
            }
            fileContent = Arrays.copyOfRange(fileContent, findMarker(fileContent, SIGNATURE_MARK) + SIGNATURE_MARK.length, fileContent.length);
            while (true) {
                if (findMarker(fileContent, TIME_MARK) != -1) {
                    byte[] temp = Base64.getDecoder().decode(Arrays.copyOfRange(fileContent, 0, findMarker(fileContent, TIME_MARK)));
                    signatures.add(Base64.getEncoder().encodeToString(temp));
                    fileContent = Arrays.copyOfRange(fileContent, findMarker(fileContent, SIGNATURE_MARK) + SIGNATURE_MARK.length, fileContent.length);
                    continue;
                }
                byte[] temp = Base64.getDecoder().decode(fileContent);
                signatures.add(Base64.getEncoder().encodeToString(temp));
                return signatures;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getOriginalFile(File signedFile) {
        try {
            byte[] fileContent = Files.readAllBytes(signedFile.toPath());
            if (findMarker(fileContent, TIME_MARK) == -1) {
                return signedFile;
            }
            byte[] originalContent = Arrays.copyOfRange(fileContent, 0, findMarker(fileContent, TIME_MARK));

            File tempFile = File.createTempFile("original_", ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(originalContent);
            }

            return tempFile;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getTime(File file, int index) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            if (findMarker(fileContent, TIME_MARK) == -1) {
                return "0000/00/00 00:00:00".getBytes();
            }
            fileContent = Arrays.copyOfRange(fileContent, findMarker(fileContent, TIME_MARK) + TIME_MARK.length, + fileContent.length);
            for (int i = 0; i <= index; i++) {
                if (i == index) {
                    fileContent = Arrays.copyOfRange(fileContent, 0, findMarker(fileContent, SIGNATURE_MARK));
                    return fileContent;
                }
                fileContent = Arrays.copyOfRange(fileContent, findMarker(fileContent, TIME_MARK) + TIME_MARK.length, fileContent.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "0000/00/00 00:00:00".getBytes();
    }

    /*/public static void decrypt(File signedFile, File originalFile, PublicKey publicKey) {
        try {
            byte[] fileContent = Files.readAllBytes(signedFile.toPath());

            int markIndex = findMarker(fileContent, SIGNATURE_MARK);
            if (markIndex == -1) {
                System.out.println("Nao foi encontrado a marca da assinatura");
            }

            byte[] originalContent = Arrays.copyOfRange(fileContent, 0, markIndex);
            byte[] signature = Arrays.copyOfRange(fileContent, markIndex + SIGNATURE_MARK.length, fileContent.length);

            String signatureStr = new String(signature);

            try (FileOutputStream fos = new FileOutputStream("testezao.pdf")) {
                fos.write(originalContent);
            }

            String hashOriginFile = Cryptography.generateHashFile(new File("testezao.pdf"));

            System.out.println(Cryptography.generateHashFile(signedFile));
            System.out.println(Cryptography.generateHashFile(originalFile));
            System.out.println(Cryptography.generateHashFile(originalContent));
            System.out.println(hashOriginFile);


            Map<String, PublicKey> publicKeys = DataBase.getPublicKeys();
            String[] cpfs = publicKeys.keySet().toArray(new String[0]);

            for (int i = 0; i < publicKeys.size(); i++) {
                if (Cryptography.verifySignature(hashOriginFile, signatureStr, publicKeys.get(cpfs[i]))) {
                    System.out.println(cpfs[i]);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    private static int findMarker(byte[] content, byte[] marker) {
        for (int i = 0; i <= content.length - marker.length; i++) {
            boolean match = true;
            for (int j = 0; j < marker.length; j++) {
                if (content[i + j] != marker[j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        File file = new File("src/main/java/utils/ATESTADO_MATRICULA_INGLES_ROGERIO_BATISTI_JUNIOR.pdf_assinado.PDF");
        String outputFile = "assinado_com_senha.pdf";
        try {
            User user = UserRepository.loadUser("12169063943", "1");

            SessionManager.getInstance().setUser(user);
            assert user != null;
            signFile(file, user.getPrivateKey());

            //decrypt(new File(outputFile), file, user.getPublicKey());

            ArrayList<String> signatures = getSignatures(new File(outputFile));

            for (String signature : signatures) {
                System.out.println(signature);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}