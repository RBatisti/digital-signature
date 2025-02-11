package model;

import java.io.StringReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class User {
    private String cpf;
    private String name;
    private KeyPair keyPair;

    public User(String cpf, String name, KeyPair keyPair) {
        this.cpf = cpf;
        this.name = name;
        this.keyPair = keyPair;
    }

    public String getName() {
        return this.name;
    }

    public String getCpf() {
        return this.cpf;
    }

    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }
}
