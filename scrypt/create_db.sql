CREATE DATABASE IF NOT EXISTS `digital-signature`;

USE `digital-signature`;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name TEXT NOT NULL,
    cpf CHAR(11) NOT NULL UNIQUE,
    salt VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    publicKey TEXT NOT NULL,
    privateKey TEXT NOT NULL
);
