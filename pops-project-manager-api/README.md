# POPS Project Manager API

API para gerenciamento de projetos e skills desenvolvida em Kotlin com Spring Boot.

## Funcionalidades

### Skills
- **POST** `/api/skills` - Cadastrar nova skill
- **GET** `/api/skills` - Listar skills ativas
- **GET** `/api/skills/{id}` - Buscar skill por ID
- **GET** `/api/skills/type/{type}` - Listar skills por tipo (SOFT/HARD)
- **PUT** `/api/skills/{id}` - Atualizar skill
- **PUT** `/api/skills/disable/{id}` - Desabilitar skill
- **PUT** `/api/skills/enable/{id}` - Habilitar skill
- **DELETE** `/api/skills/{id}` - Excluir skill

### Projects
- **POST** `/api/projects` - Cadastrar novo projeto
- **GET** `/api/projects` - Listar projetos ativos
- **GET** `/api/projects/{id}` - Buscar projeto por ID
- **GET** `/api/projects/status/{status}` - Listar projetos por status
- **PUT** `/api/projects/{id}` - Atualizar projeto
- **PUT** `/api/projects/disable/{id}` - Desabilitar projeto
- **PUT** `/api/projects/enable/{id}` - Habilitar projeto
- **DELETE** `/api/projects/{id}` - Excluir projeto

## Tecnologias

- Kotlin 1.9.22
- Spring Boot 3.5.3
- Spring Data JPA
- MySQL
- Swagger/OpenAPI
- Maven

## Configuração

1. Configure as variáveis de ambiente para conexão com MySQL:
   - `IPV4_PRIVATE` - IP do servidor MySQL
   - `MYSQL_DATABASE` - Nome do banco de dados
   - `MYSQL_USER` - Usuário do MySQL
   - `MYSQL_PASSWORD` - Senha do MySQL

2. Execute o projeto:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Acesse a documentação da API:
   - Swagger UI: http://localhost:8081/api/swagger-ui.html
   - OpenAPI JSON: http://localhost:8081/api/v3/api-docs

## Modelos de Dados

### Skill
- `id`: Long (auto-gerado)
- `name`: String (obrigatório, máximo 100 caracteres)
- `description`: String (opcional, máximo 500 caracteres)
- `type`: SkillType (SOFT ou HARD)
- `active`: Boolean (padrão: true)

### Project
- `id`: Long (auto-gerado)
- `name`: String (obrigatório, máximo 200 caracteres)
- `type`: String (opcional, máximo 50 caracteres)
- `description`: String (opcional, máximo 1000 caracteres)
- `status`: ProjectStatus (PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED)
- `budget`: BigDecimal (opcional)
- `startDate`: LocalDate (opcional)
- `endDate`: LocalDate (opcional)
- `area`: String (opcional, máximo 100 caracteres)
- `active`: Boolean (padrão: true)
- `requiredSkills`: Set<Skill> (opcional)




