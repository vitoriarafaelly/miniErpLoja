# Mini ERP Loja - API REST 

Este projeto é um **microserviço em Spring Boot** (Java 17) que expõe uma **API REST** para gestão de clientes, produtos e pedidos.  
Ele utiliza **PostgreSQL** como banco de dados, **Liquibase** para versionamento de schema e roda em **Docker Compose** para garantir portabilidade e isolamento dos serviços.

### Motivos da escolha da arquitetura

API REST: comunicação simples, escalável e independente de plataforma.

Microserviço: a aplicação é isolada, podendo ser implantada e escalada separadamente de outros módulos.

### Tecnologias principais

Java 17

Spring Boot

PostgreSQL

Liquibase

Docker & Docker Compose

PgAdmin

### Subir a aplicação
```bash 
docker-compose up --build
```
### Parar aplicação
```bash
docker-compose down
```
### Acessos

API REST → http://localhost:8080

PgAdmin → http://localhost:5050

Usuário: admin@admin.com

Senha: admin

Postgres → localhost:5432

Banco: loja_db

Usuário: admin

Senha: password

### Swagger
http://localhost:8080/swagger-ui/index.html#/

### Observabilidade
http://localhost:8080/actuator/prometheus