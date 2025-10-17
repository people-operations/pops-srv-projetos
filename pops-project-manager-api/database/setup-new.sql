-- Script para criar o banco de dados e tabelas do POPS Project Manager API (Versão Escalável)
-- Execute este script no MySQL para configurar o banco de dados

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS pops_project_manager 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE pops_project_manager;

-- Criar tabela de tipos de skill
CREATE TABLE IF NOT EXISTS skill_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criar tabela de tipos de projeto
CREATE TABLE IF NOT EXISTS project_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criar tabela de status de projeto
CREATE TABLE IF NOT EXISTS project_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criar tabela de skills
CREATE TABLE IF NOT EXISTS skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    skill_type_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (skill_type_id) REFERENCES skill_type(id)
);

-- Criar tabela de projetos
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    project_type_id BIGINT,
    description VARCHAR(1000),
    project_status_id BIGINT NOT NULL,
    budget DECIMAL(15,2),
    start_date DATE,
    end_date DATE,
    area VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (project_type_id) REFERENCES project_type(id),
    FOREIGN KEY (project_status_id) REFERENCES project_status(id)
);

-- Criar tabela de relacionamento entre projetos e skills
CREATE TABLE IF NOT EXISTS project_skills (
    project_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, skill_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

-- Inserir dados de exemplo para tipos de skill
INSERT INTO skill_type (name, description) VALUES
('HARD', 'Habilidades técnicas e conhecimentos específicos'),
('SOFT', 'Habilidades comportamentais e interpessoais'),
('MANAGEMENT', 'Habilidades de gestão e liderança'),
('ANALYTICS', 'Habilidades de análise de dados e métricas');

-- Inserir dados de exemplo para tipos de projeto
INSERT INTO project_type (name, description) VALUES
('DESENVOLVIMENTO', 'Projetos de desenvolvimento de software'),
('WEB', 'Projetos de desenvolvimento web'),
('MOBILE', 'Projetos de desenvolvimento mobile'),
('INFRAESTRUTURA', 'Projetos de infraestrutura e DevOps'),
('BI', 'Projetos de Business Intelligence e Analytics'),
('CONSULTORIA', 'Projetos de consultoria e análise');

-- Inserir dados de exemplo para status de projeto
INSERT INTO project_status (name, description) VALUES
('PLANNING', 'Projeto em fase de planejamento'),
('IN_PROGRESS', 'Projeto em andamento'),
('ON_HOLD', 'Projeto pausado temporariamente'),
('COMPLETED', 'Projeto concluído com sucesso'),
('CANCELLED', 'Projeto cancelado');

-- Inserir dados de exemplo para skills
INSERT INTO skill (name, description, skill_type_id) VALUES
-- Hard Skills
('Java', 'Linguagem de programação Java', 1),
('Kotlin', 'Linguagem de programação Kotlin', 1),
('Spring Boot', 'Framework Spring Boot para desenvolvimento de APIs', 1),
('MySQL', 'Sistema de gerenciamento de banco de dados MySQL', 1),
('Docker', 'Plataforma de containerização', 1),
('Git', 'Sistema de controle de versão', 1),
('React', 'Biblioteca JavaScript para interfaces de usuário', 1),
('Node.js', 'Runtime JavaScript para desenvolvimento backend', 1),
-- Soft Skills
('Comunicação', 'Habilidade de comunicação interpessoal', 2),
('Liderança', 'Capacidade de liderar equipes', 2),
('Trabalho em Equipe', 'Habilidade de trabalhar colaborativamente', 2),
('Resolução de Problemas', 'Capacidade de analisar e resolver problemas', 2),
('Gestão de Tempo', 'Habilidade de gerenciar tempo e prioridades', 2),
('Adaptabilidade', 'Capacidade de se adaptar a mudanças', 2),
-- Management Skills
('Gestão de Projetos', 'Habilidade em gerenciar projetos', 3),
('Gestão de Equipes', 'Habilidade em gerenciar equipes', 3),
('Scrum', 'Metodologia ágil Scrum', 3),
-- Analytics Skills
('Power BI', 'Ferramenta de Business Intelligence', 4),
('Python', 'Linguagem de programação para análise de dados', 4),
('SQL Avançado', 'Consultas SQL complexas e otimização', 4);

-- Inserir dados de exemplo para projetos
INSERT INTO project (name, project_type_id, description, project_status_id, budget, start_date, end_date, area) VALUES
('Sistema de Gestão de Projetos', 1, 'Sistema completo para gerenciar projetos, equipes e recursos', 2, 50000.00, '2024-09-01', '2024-12-31', 'Tecnologia'),
('Portal do Cliente', 2, 'Portal web responsivo para clientes acessarem serviços e informações', 1, 75000.00, '2024-10-15', '2025-02-15', 'Tecnologia'),
('App Mobile de Vendas', 3, 'Aplicativo mobile para equipe de vendas externas', 1, 120000.00, '2024-11-01', '2025-05-01', 'Tecnologia'),
('Migração de Dados', 4, 'Migração de dados legados para nova arquitetura', 3, 30000.00, '2024-08-01', '2024-10-31', 'Infraestrutura'),
('Dashboard Analytics', 5, 'Dashboard para análise de métricas e KPIs', 4, 25000.00, '2024-06-01', '2024-08-31', 'Analytics');

-- Inserir relacionamentos entre projetos e skills
INSERT INTO project_skills (project_id, skill_id) VALUES
-- Sistema de Gestão de Projetos
(1, 1), -- Java
(1, 2), -- Kotlin
(1, 3), -- Spring Boot
(1, 4), -- MySQL
(1, 9), -- Comunicação
(1, 10), -- Liderança
(1, 11), -- Trabalho em Equipe
(1, 15), -- Gestão de Projetos
-- Portal do Cliente
(2, 1), -- Java
(2, 2), -- Kotlin
(2, 3), -- Spring Boot
(2, 4), -- MySQL
(2, 7), -- React
(2, 9), -- Comunicação
(2, 11), -- Trabalho em Equipe
(2, 12), -- Resolução de Problemas
-- App Mobile de Vendas
(3, 1), -- Java
(3, 2), -- Kotlin
(3, 5), -- Docker
(3, 6), -- Git
(3, 9), -- Comunicação
(3, 10), -- Liderança
(3, 13), -- Gestão de Tempo
-- Migração de Dados
(4, 4), -- MySQL
(4, 5), -- Docker
(4, 6), -- Git
(4, 12), -- Resolução de Problemas
(4, 14), -- Adaptabilidade
-- Dashboard Analytics
(5, 1), -- Java
(5, 3), -- Spring Boot
(5, 4), -- MySQL
(5, 19), -- Power BI
(5, 20), -- Python
(5, 9), -- Comunicação
(5, 12); -- Resolução de Problemas

-- Verificar os dados inseridos
SELECT 'Tipos de Skill cadastrados:' as info;
SELECT id, name, description, active FROM skill_type ORDER BY name;

SELECT 'Tipos de Projeto cadastrados:' as info;
SELECT id, name, description, active FROM project_type ORDER BY name;

SELECT 'Status de Projeto cadastrados:' as info;
SELECT id, name, description, active FROM project_status ORDER BY name;

SELECT 'Skills cadastradas:' as info;
SELECT s.id, s.name, st.name as tipo, s.active 
FROM skill s 
JOIN skill_type st ON s.skill_type_id = st.id 
ORDER BY st.name, s.name;

SELECT 'Projetos cadastrados:' as info;
SELECT p.id, p.name, pt.name as tipo, ps.name as status, p.budget, p.area 
FROM project p 
LEFT JOIN project_type pt ON p.project_type_id = pt.id 
JOIN project_status ps ON p.project_status_id = ps.id 
ORDER BY ps.name, p.name;

SELECT 'Relacionamentos projeto-skill:' as info;
SELECT p.name as projeto, s.name as skill, st.name as tipo_skill
FROM project p
JOIN project_skills ps ON p.id = ps.project_id
JOIN skill s ON ps.skill_id = s.id
JOIN skill_type st ON s.skill_type_id = st.id
ORDER BY p.name, s.name;




