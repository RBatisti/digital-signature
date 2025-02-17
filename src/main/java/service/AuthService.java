package service;

import dao.DB;
import model.User;

import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Objects;

import static dao.UserDAO.*;
import static security.CryptoUtils.*;

public class AuthService {
    public static void createUser(String cpf, String password, String name) {
        byte[] salt = generateSalt();
        String passwordHash = generateHashPassword(password, salt);
        String saltStr = Base64.getEncoder().encodeToString(salt);
        KeyPair keyPair = keyPairGenerator();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        byte[] keyDecrypt = generateKeyDecrypt(password, salt);

        String privateKeyEncrypted = encrypt(keyDecrypt, privateKey);

        saveUser(saltStr, passwordHash, publicKey, privateKeyEncrypted, cpf, name);
    }

    public static boolean checkLogin(String cpf, String password) {
        if (existCpf(cpf)) {
            return Objects.equals(getPassword(cpf), generateHashPassword(password, getSalt(cpf)));
        }
        return false;
    }

    public static User loadUser(String cpf, String password) {
        try {
            Connection connection = DB.getConnection();

            String query = "SELECT salt, publicKey, privateKey FROM `digital-signature`.users where cpf = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpf);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                return null;
            }

            if (resultSet.next()) {
                String salt = resultSet.getString("salt");
                byte[] saltBytes = Base64.getDecoder().decode(salt);
                byte[] keyDecrypt = generateKeyDecrypt(password, saltBytes);

                String publicKey = resultSet.getString("publicKey");
                String privateKey = resultSet.getString("privateKey");

                privateKey = decrypt(keyDecrypt, privateKey);

                KeyPair keyPair = loadKeyPair(publicKey, privateKey);

                return new User(keyPair);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
