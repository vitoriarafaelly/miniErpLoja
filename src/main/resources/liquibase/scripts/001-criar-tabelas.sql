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

CREATE TABLE pedidos (
                         id BIGSERIAL PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         subtotal NUMERIC(10,2) NOT NULL DEFAULT 0.00,
                         descontos NUMERIC(10,2) DEFAULT 0.00,
                         total NUMERIC(10,2) NOT NULL DEFAULT 0.00,
                         status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
                         data_hora_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id)
                             REFERENCES clientes (id) ON DELETE RESTRICT
);

CREATE TABLE itens_pedido (
                              id BIGSERIAL PRIMARY KEY,
                              pedido_id BIGINT NOT NULL,
                              produto_id BIGINT NOT NULL,
                              quantidade INT NOT NULL CHECK (quantidade > 0),
                              desconto NUMERIC(10,2),
                              subtotal NUMERIC(10,2) NOT NULL,

                              CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id)
                                  REFERENCES pedidos (id) ON DELETE CASCADE,

                              CONSTRAINT fk_item_produto FOREIGN KEY (produto_id)
                                  REFERENCES produtos (id)
);
