# 💰 Gestão Bancária

API e frontend para gerenciamento de contas bancárias, clientes e movimentações financeiras.

Permite operações de **depósito**, **saque**, **cadastro de pessoas e contas**, com validações de **CPF** e **saldo disponível**.

---

## 🚀 Pré-requisitos

Antes de começar, você precisa ter instalado:

- [Java 21 (JDK)](https://www.oracle.com/br/java/technologies/downloads/#jdk21-windows)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop)
- [Git](https://git-scm.com/downloads)
- [Node.js + NPM](https://nodejs.org/) (para executar o frontend)

---

## 📥 Clonando o repositório

```bash
git clone https://github.com/SENAI-SD/prova-java-junior-00933-2025-027.007.911-42
cd prova-java-junior-00933-2025-027.007.911-42
```

---

## 🖥️ Executando Manualmente

### 📦 Backend (Spring Boot)

1. **Suba o banco de dados** (na pasta raiz):

   ```bash
   docker-compose up -d gestaoBancaria_db
   ```

2. **Compile o projeto** (ainda dentro de `/api`):

   ```bash
   mvn clean install
   ```

3. **Execute o servidor Spring Boot**:

   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a API**:

   [http://localhost:8080/api](http://localhost:8080/api)

---

### 💻 Frontend (Next.js)

1. Vá para a pasta do frontend (`/app`) e instale as dependências:

   ```bash
   npm install
   # ou
   yarn
   ```

2. **Execute o frontend**:

   ```bash
   npm run dev
   # ou
   yarn dev
   ```

3. **Acesse o frontend**:

   [http://localhost:3000](http://localhost:3000)

---

## 🐳 Executando com Docker (full stack)

Na raiz do projeto, execute:

```bash
docker-compose up --build
```

Os serviços serão iniciados:

- Backend: [http://localhost:8080/api](http://localhost:8080/api)
- Frontend: [http://localhost:3000](http://localhost:3000)

---

## 📖 Documentação da API (Swagger)

Após o backend estar no ar, acesse:

📄 [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
