package model;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class User {
    private final KeyPair keyPair;

    public User(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }
}
