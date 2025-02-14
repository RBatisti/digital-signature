package model;

public class SignatureData {
    private final String cpf;
    private final String name;
    private final String date;

    public SignatureData(String cpf, String name, String date) {
        this.cpf = cpf;
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getDate() {
        return date;
    }
}
