# Como Rodar a Aplicação com MySQL

## 🎯 **Comandos para Rodar com MySQL**

```powershell
# 1. Ir para o diretório
cd pops-project-manager-api

# 2. Configurar Java
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# 3. Rodar com perfil MySQL
.\mvnw.cmd spring-boot:run -Dspring.profiles.active=mysql
```

## 📊 **Verificar Conexão**

### **1. Swagger UI**
```
http://localhost:8081/api/swagger-ui.html
```

## 🗄️ **Banco MySQL**

### **Configuração Atual:**
- **Host:** localhost
- **Porta:** 3306
- **Database:** pops_project_manager
- **Usuário:** root
- **Senha:** gyulia06*

### **Script SQL:**
Execute o arquivo `database/setup-new.sql` no MySQL antes de rodar a aplicação.

## ⚠️ **Solução de Problemas**

### **Erro de Conexão MySQL:**
1. Verifique se o MySQL está rodando
2. Confirme as credenciais
3. Execute o script `database/setup-new.sql`

### **Aplicação não inicia:**
1. Verifique se a porta 8082 está livre
2. Confirme se o Java 17+ está instalado
3. Verifique os logs de erro



