CREATE TABLE clientes (
                          id SERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          cpf VARCHAR(14) NOT NULL UNIQUE,
                          logradouro VARCHAR(255),
                          numero VARCHAR(10),
                          complemento VARCHAR(255),
                          bairro VARCHAR(255),
                          cidade VARCHAR(255),
                          uf CHAR(2),
                          cep VARCHAR(8) NOT NULL
);

CREATE TABLE produtos (
                          id SERIAL PRIMARY KEY,
                          sku VARCHAR(50) NOT NULL,
                          nome VARCHAR(255) NOT NULL,
                          precoBruto DECIMAL(10, 2) NOT NULL,
                          estoque INT NOT NULL DEFAULT 0,
                          estoqueMinimo INT NOT NULL DEFAULT 0,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE
);