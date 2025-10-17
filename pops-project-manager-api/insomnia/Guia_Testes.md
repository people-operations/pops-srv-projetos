# 📘 POPS Project Manager API – Guia Técnico e Prático de Testes

## 🚀 Testando os Endpoints

### Ordem Recomendada de Testes:
1. **Skill Types** → Criar e gerenciar tipos de habilidades  
2. **Skills** → Cadastrar e relacionar habilidades com tipos  
3. **Project Types** → Cadastrar tipos de projeto  
4. **Project Status** → Cadastrar status de projeto  
5. **Projects** → Criar e gerenciar projetos (com skills e status)

---

## 🧩 1. Skill Types

### Endpoints Principais
| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/skill-types` | Lista tipos de skills ativos |
| `POST` | `/skill-types` | Cria um novo tipo de skill |
| `GET` | `/skill-types/{id}` | Busca tipo de skill por ID |
| `PATCH` | `/skill-types/{id}` | Atualiza tipo de skill |
| `DELETE` | `/skill-types/{id}` | Remove tipo de skill |
| `PUT` | `/skill-types/enable/{id}` | Ativa tipo de skill |
| `PUT` | `/skill-types/disable/{id}` | Desativa tipo de skill |
| `GET` | `/skill-types/inactive` | Lista tipos de skill inativos |

### 📝 Exemplo de Payload – Criar Tipo de Skill
```json
{
  "name": "HARD",
  "description": "Habilidades técnicas e específicas"
}
```

### 🔍 Respostas Esperadas
- **200 OK** – Tipo criado ou retornado com sucesso  
- **400 Bad Request** – Campos obrigatórios ausentes (`name`)  
- **404 Not Found** – Tipo não encontrado  
- **500 Internal Server Error** – Erro inesperado

---

## 🧠 2. Skills

### Endpoints Principais
| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/skills` | Lista skills ativas |
| `POST` | `/skills` | Cria nova skill |
| `GET` | `/skills/{id}` | Busca skill por ID |
| `PATCH` | `/skills/{id}` | Atualiza skill |
| `DELETE` | `/skills/{id}` | Remove skill |
| `PUT` | `/skills/enable/{id}` | Ativa skill |
| `PUT` | `/skills/disable/{id}` | Desativa skill |
| `GET` | `/skills/inactive` | Lista skills inativas |
| `GET` | `/skills/type/{typeId}` | Lista skills por tipo |
| `GET` | `/skills/pageable` | Lista skills paginadas |

### 📝 Exemplo de Payload – Criar Skill
```json
{
  "name": "React",
  "description": "Biblioteca para criação de interfaces web",
  "typeId": 1
}
```

### 🧩 Exemplo de Payload – Atualizar Skill
```json
{
  "name": "ReactJS",
  "description": "Atualização de descrição",
  "typeId": 1
}
```

### 🔍 Respostas Esperadas
- **200 OK** – Skill criada, atualizada ou listada com sucesso  
- **400 Bad Request** – Campos obrigatórios ausentes (`name`, `typeId`)  
- **404 Not Found** – Skill não encontrada  
- **500 Internal Server Error** – Erro interno do servidor

---

## 🧱 3. Project Types

### Endpoints Principais
| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/project-types` | Lista tipos de projeto ativos |
| `POST` | `/project-types` | Cria novo tipo de projeto |
| `GET` | `/project-types/{id}` | Busca tipo de projeto por ID |
| `PATCH` | `/project-types/{id}` | Atualiza tipo de projeto |
| `DELETE` | `/project-types/{id}` | Remove tipo de projeto |
| `PUT` | `/project-types/enable/{id}` | Ativa tipo de projeto |
| `PUT` | `/project-types/disable/{id}` | Desativa tipo de projeto |
| `GET` | `/project-types/inactive` | Lista tipos inativos |

### 📝 Exemplo de Payload – Criar Tipo de Projeto
```json
{
  "name": "Desenvolvimento Web",
  "description": "Projetos voltados para aplicações web"
}
```

### 🔍 Respostas Esperadas
- **200 OK** – Tipo criado ou listado com sucesso  
- **400 Bad Request** – Dados inválidos  
- **404 Not Found** – Tipo não encontrado

---

## 📊 4. Project Status

### Endpoints Principais
| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/project-status` | Lista status ativos |
| `POST` | `/project-status` | Cria novo status |
| `GET` | `/project-status/{id}` | Busca status por ID |
| `PATCH` | `/project-status/{id}` | Atualiza status |
| `DELETE` | `/project-status/{id}` | Remove status |
| `PUT` | `/project-status/enable/{id}` | Ativa status |
| `PUT` | `/project-status/disable/{id}` | Desativa status |
| `GET` | `/project-status/inactive` | Lista status inativos |

### 📝 Exemplo de Payload – Criar Status
```json
{
  "name": "PLANNING",
  "description": "Projeto em fase de planejamento"
}
```

### 🔍 Respostas Esperadas
- **200 OK** – Status criado ou retornado com sucesso  
- **400 Bad Request** – Dados inválidos  
- **404 Not Found** – Status não encontrado

---

## 🧮 5. Projects

### Endpoints Principais
| Método | Endpoint | Descrição |
|--------|-----------|------------|
| `GET` | `/projects` | Lista projetos ativos |
| `POST` | `/projects` | Cria novo projeto |
| `GET` | `/projects/{id}` | Busca projeto por ID |
| `PATCH` | `/projects/{id}` | Atualiza projeto |
| `DELETE` | `/projects/{id}` | Remove projeto |
| `PUT` | `/projects/enable/{id}` | Reativa projeto |
| `PUT` | `/projects/disable/{id}` | Desativa projeto |
| `GET` | `/projects/status/{statusId}` | Lista projetos por status |
| `GET` | `/projects/inactive` | Lista projetos inativos |
| `GET` | `/projects/pageable` | Lista paginada de projetos |

### 📝 Exemplo de Payload – Criar Projeto
```json
{
  "name": "Sistema de E-commerce",
  "typeId": 1,
  "description": "Sistema completo de e-commerce",
  "statusId": 1,
  "budget": 150000.00,
  "startDate": "2024-11-01",
  "endDate": "2025-06-30",
  "area": "Tecnologia",
  "skillIds": [1, 2, 3]
}
```

### 🧩 Exemplo de Payload – Atualizar Projeto
```json
{
  "name": "Sistema de E-commerce v2",
  "description": "Atualização de escopo do projeto",
  "statusId": 2,
  "budget": 180000.00,
  "skillIds": [1, 4, 5]
}
```

### 🔍 Respostas Esperadas
- **200 OK** – Projeto criado, atualizado ou listado com sucesso  
- **400 Bad Request** – Dados inválidos ou campos ausentes  
- **404 Not Found** – Projeto não encontrado  
- **500 Internal Server Error** – Erro interno

---

## 🔍 Verificando Respostas

### Respostas de Sucesso:
- **200 OK** – Operação realizada com sucesso  
- **201 Created** – Recurso criado com sucesso  
- **204 No Content** – Recurso excluído com sucesso  

### Respostas de Erro:
- **400 Bad Request** – Dados inválidos  
- **404 Not Found** – Recurso não encontrado  
- **500 Internal Server Error** – Erro interno do servidor

---

## 🐛 Solução de Problemas

### Erro 404
```
404 Not Found
```
**Causas comuns:**
- ID inexistente ou já removido  
- Endpoint incorreto

**Solução:**
1. Verifique o ID informado  
2. Confira a URL usada  
3. Teste novamente com outro registro

### Erro 400
```
400 Bad Request
```
**Causas comuns:**
- Campos obrigatórios ausentes  
- Tipos de dados incorretos

**Solução:**
1. Valide o JSON enviado  
2. Verifique se `name`, `statusId` e `skillIds` estão presentes (em projetos)  
3. Revise o formato do corpo da requisição

### Erro 500
```
500 Internal Server Error
```
**Causas comuns:**
- Falha na lógica do backend  
- Dados inconsistentes (ex: IDs que referenciam registros inexistentes)

**Solução:**
1. Revise os relacionamentos (`typeId`, `statusId`, `skillIds`)  
2. Verifique se os registros usados já foram criados anteriormente

---

## 📊 Dica de Testes em Sequência

1. **Criar Skill Type** → `/skill-types`  
2. **Criar Skills** → `/skills`  
3. **Criar Project Type** → `/project-types`  
4. **Criar Project Status** → `/project-status`  
5. **Criar Project** → `/projects`  
6. **Consultar / Atualizar / Desabilitar / Habilitar** → conforme necessário  

---

## ✅ Conclusão
A **POPS Project Manager API** oferece um fluxo completo de **CRUD com controle de ativação/inativação**, permitindo gerenciar **skills**, **tipos**, **status** e **projetos** de forma integrada.  
Este guia foi criado para uso direto no **Postman**, com exemplos fiéis ao Swagger oficial (v3.1.0).
