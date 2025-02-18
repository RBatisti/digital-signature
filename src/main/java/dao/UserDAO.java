package dao;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    public static void saveUser(String salt, String hashPassword, String publicKey, String privateKey, String cpf, String name) {
        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO `digital-signature`.users"
                            + "(cpf, salt, password, publicKey, privateKey, name)"
                            + "VALUES"
                            + "(?, ?, ?, ?, ?, ?)");

            statement.setString(1, cpf);
            statement.setString(2, salt);
            statement.setString(3, hashPassword);
            statement.setString(4, publicKey);
            statement.setString(5, privateKey);
            statement.setString(6, name);

            statement.executeUpdate();
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getName(String cpf) {
        String query = "SELECT name FROM `digital-signature`.users WHERE cpf = ?";
        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpf);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public static Map<String, PublicKey> getPublicKeys() {
        Map<String, PublicKey> publicKeys = new HashMap<>();
        String query = "SELECT cpf, publicKey FROM `digital-signature`.users";
        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String publicKeyStr = resultSet.getString("publicKey");
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                publicKeys.put(resultSet.getString("cpf"), keyFactory.generatePublic(keySpec));
            }
            return publicKeys;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] getSalt(String cpf) {
        String query = "SELECT salt FROM `digital-signature`.users WHERE cpf = ?";
        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpf);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Base64.getDecoder().decode(resultSet.getString("salt"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static boolean existCpf(String cpf) {
        String query = "SELECT cpf FROM `digital-signature`.users";
        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getString("cpf").equals(cpf)) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPassword(String cpf) {
        String query = "SELECT password FROM `digital-signature`.users WHERE cpf = ?";

        try {
            Connection connection = DB.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cpf);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("password");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
