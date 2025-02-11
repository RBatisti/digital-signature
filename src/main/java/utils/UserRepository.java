package utils;

import config.Config;
import dao.DB;
import model.User;
import org.bouncycastle.jcajce.provider.symmetric.ARC4;

import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

public class UserRepository {
    public static void createUser(String cpf, String password, String name) {
        byte[] salt = Cryptography.generateSalt();
        String passwordHash = Cryptography.generateHashPassword(password, salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);
        KeyPair keyPair = Cryptography.keyPairGenerator();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        byte[] keyDecrypt = Cryptography.generateKeyDecrypt(password, salt);

        String privateKeyEncrypted = Cryptography.encrypt(keyDecrypt, privateKey);

        DataBase.saveUser(saltStr, passwordHash, publicKey, privateKeyEncrypted, cpf, name);

        System.out.println(cpf);
        System.out.println(password);
        System.out.println(name);
    }

    public static boolean checkLogin(String cpf, String password) {
        if (DataBase.existCpf(cpf)) {
            return DataBase.getPassword(cpf).equals(Cryptography.generateHashPassword(password, DataBase.getSalt(cpf)));
        }
        return false;
    }

    public static User loadUser(String cpf, String password) {
        try {
            Connection connection = DB.getConnection();

            String query = "SELECT salt, publicKey, privateKey, name FROM `digital-signature`.users where cpf = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpf);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                return null;
            }

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String salt = resultSet.getString("salt");
                byte[] saltBytes = Base64.getDecoder().decode(salt);
                byte[] keyDecrypt = Cryptography.generateKeyDecrypt(password, saltBytes);

                String publicKey = resultSet.getString("publicKey");
                String privateKey = resultSet.getString("privateKey");

                privateKey = Cryptography.decrypt(keyDecrypt, privateKey);

                KeyPair keyPair = Cryptography.loadKeyPair(publicKey, privateKey);

                System.out.println("RETORNOU CERTINHO");

                return new User(cpf, name, keyPair);
            }
            // Talvez quando foi convertida a key para string houve algum problema

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
