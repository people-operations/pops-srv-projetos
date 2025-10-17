# 💼 POPS Project Manager API

> API REST para gerenciamento de projetos, tipos, status e habilidades (skills).  
> Desenvolvida em **Kotlin + Spring Boot**, com arquitetura modular e integração com **MySQL**.

---

## 🚀 Visão Geral

A **POPS Project Manager API** é uma aplicação backend criada para centralizar o gerenciamento de **projetos** e **competências técnicas e comportamentais (skills)**.  
Ela permite cadastrar, atualizar e desativar entidades como **projetos**, **tipos de projetos**, **status de andamento**, **tipos de skills** e **skills**. 

### 🎯 Principais Objetivos
- Facilitar o **gerenciamento de projetos** dentro de equipes multidisciplinares  
- Mapear **skills técnicas e comportamentais** necessárias para cada projeto  
- Prover **CRUD completo** com controle de **ativação e inativação**  
- Permitir **integração com sistemas externos** via endpoints REST

---

## 🧱 Tecnologias Utilizadas

| Camada | Tecnologia |
|--------|-------------|
| Linguagem | **Kotlin (JDK 17+)** |
| Framework | **Spring Boot 3.5.1** |
| Banco de Dados | **MySQL** |
| ORM | **Spring Data JPA (Hibernate)** |
| Documentação | **OpenAPI / Swagger v3.1.0** |
| Build | **Maven** |

---

## ⚙️ Execução Local

### Pré-requisitos
- JDK 17+  
- Maven 3.8+  
- Banco de dados **MySQL** rodando localmente  
- IntelliJ IDEA ou VS Code com suporte a Kotlin

### Passos
```bash
# Clonar o repositório
git clone https://github.com/people-operations/pops-srv-projetos.git
cd pops-srv-projetos
cd pops-project-manager-api

# Configurar Java
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Compilar e rodar
mvn spring-boot:run ou .\mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
```

A aplicação subirá por padrão em:
```
http://localhost:8082/api
```
---

## 📘 Documentação da API

O Swagger UI é gerado automaticamente:

```
http://localhost:8082/swagger-ui/index.html
```

Arquivo OpenAPI local:
```
/v3/api-docs
```

---

## 🧩 Estrutura de Endpoints

| Grupo | Prefixo | Descrição |
|--------|----------|-----------|
| **Skills** | `/skills` | CRUD completo de habilidades |
| **Skill Types** | `/skill-types` | Tipos de skills (ex: HARD / SOFT) |
| **Projects** | `/projects` | Gerenciamento de projetos e suas skills |
| **Project Types** | `/project-types` | Classificação dos tipos de projeto |
| **Project Status** | `/project-status` | Controle de status (Planning, Active, Finished, etc.) |

Cada grupo possui endpoints para:
- `GET` listar  
- `POST` criar  
- `PATCH` atualizar  
- `DELETE` remover  
- `PUT /enable/{id}` ativar  
- `PUT /disable/{id}` desativar  

---

## 🤝 Contribuindo

1. Faça um fork do repositório  
2. Crie uma branch para sua feature:
   ```bash
   git checkout -b feature/minha-feature
   ```
3. Commit suas mudanças:
   ```bash
   git commit -m "feat: adiciona nova funcionalidade"
   ```
4. Envie um Pull Request 🚀


---

## ✨ Desenvolvido por
👩‍💻 **POPS Team** – [gyulia.piqueira@sptech.school](mailto:gyulia.piqueira@sptech.school)
