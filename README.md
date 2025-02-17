# Digital Signature
## 📌 Sobre o Projeto
Este projeto é um sistema de assinatura digital desenvolvido em Java utilizando JavaFX para a interface gráfica, MySQL como banco de dados e Bouncy Castle para operações criptográficas. O sistema permite que usuários criem contas, assinem arquivos digitalmente, exportem os arquivos assinados e validem assinaturas.

## 🚀 Funcionalidades
- Criação de diversos usuários independentes.
- Assinar arquivos com a chave privada.
- Validar arquivos através da chave pública.
- Possiblidade de diversas assinaturas em um mesmo arquivo.

## 🛠️ Tecnologias Utilizadas
- **Java**
- **JavaFX** (para a interface gráfica)
- **MySQL** (para armazenar os dados)
- **JDBC** (para conexão com o banco de dados)
- **RSA** (para assinatura)
- **SCrypt** (para hash da senha)
- **SHA-256** (para hash do arquivo)
- **AES** (para criptografar a chave privada)
- **Maven** (para gerenciamento de dependências)

## ⚠️ Pré-requisitos

1. **Banco de Dados MySQL**: O projeto usa o MySQL para armazenar dados. Siga os passos abaixo para configurar o banco de dados:

## Configuração do Banco de Dados

### Passo 1: Criação do Banco de Dados
- Abra o MySQL ou outro cliente SQL de sua escolha.
- Execute o script SQL fornecido na pasta `scrypt/create_db.sql` para criar o banco de dados e as tabelas.

### Passo 2: Configuração do `db.properties`
- Certifique-se de configurar corretamente a conexão com o banco de dados no arquivo `db.properties` na pasta `src/main/java/config`.

Exemplo de configuração:

```properties
user=seu_usuario
password=sua_senha
dburl=jdbc:mysql://localhost:3306/digital-signature
```

