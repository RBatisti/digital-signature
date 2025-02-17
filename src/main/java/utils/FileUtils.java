package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import static config.Config.SIGNATURE_MARK;
import static config.Config.TIME_MARK;

public class FileUtils {
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
}
