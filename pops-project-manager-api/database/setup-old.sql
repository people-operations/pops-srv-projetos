-- Script para criar o banco de dados e tabelas do POPS Project Manager API
-- Execute este script no MySQL para configurar o banco de dados

-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS pops_project_manager 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE pops_project_manager;

-- Criar tabela de skills
CREATE TABLE IF NOT EXISTS skill (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    type ENUM('SOFT', 'HARD') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criar tabela de projetos
CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    type VARCHAR(50),
    description VARCHAR(1000),
    status ENUM('PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED') NOT NULL,
    budget DECIMAL(15,2),
    start_date DATE,
    end_date DATE,
    area VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Criar tabela de relacionamento entre projetos e skills
CREATE TABLE IF NOT EXISTS project_skills (
    project_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, skill_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

-- Inserir dados de exemplo para skills
INSERT INTO skill (name, description, type) VALUES
('Java', 'Linguagem de programação Java', 'HARD'),
('Kotlin', 'Linguagem de programação Kotlin', 'HARD'),
('Spring Boot', 'Framework Spring Boot para desenvolvimento de APIs', 'HARD'),
('MySQL', 'Sistema de gerenciamento de banco de dados MySQL', 'HARD'),
('Docker', 'Plataforma de containerização', 'HARD'),
('Git', 'Sistema de controle de versão', 'HARD'),
('Comunicação', 'Habilidade de comunicação interpessoal', 'SOFT'),
('Liderança', 'Capacidade de liderar equipes', 'SOFT'),
('Trabalho em Equipe', 'Habilidade de trabalhar colaborativamente', 'SOFT'),
('Resolução de Problemas', 'Capacidade de analisar e resolver problemas', 'SOFT'),
('Gestão de Tempo', 'Habilidade de gerenciar tempo e prioridades', 'SOFT'),
('Adaptabilidade', 'Capacidade de se adaptar a mudanças', 'SOFT');

-- Inserir dados de exemplo para projetos
INSERT INTO project (name, type, description, status, budget, start_date, end_date, area) VALUES
('Sistema de Gestão de Projetos', 'Desenvolvimento', 'Sistema completo para gerenciar projetos, equipes e recursos', 'IN_PROGRESS', 50000.00, '2024-09-01', '2024-12-31', 'Tecnologia'),
('Portal do Cliente', 'Desenvolvimento Web', 'Portal web responsivo para clientes acessarem serviços e informações', 'PLANNING', 75000.00, '2024-10-15', '2025-02-15', 'Tecnologia'),
('App Mobile de Vendas', 'Desenvolvimento Mobile', 'Aplicativo mobile para equipe de vendas externas', 'PLANNING', 120000.00, '2024-11-01', '2025-05-01', 'Tecnologia'),
('Migração de Dados', 'Infraestrutura', 'Migração de dados legados para nova arquitetura', 'ON_HOLD', 30000.00, '2024-08-01', '2024-10-31', 'Infraestrutura'),
('Dashboard Analytics', 'Business Intelligence', 'Dashboard para análise de métricas e KPIs', 'COMPLETED', 25000.00, '2024-06-01', '2024-08-31', 'Analytics');

-- Inserir relacionamentos entre projetos e skills
INSERT INTO project_skills (project_id, skill_id) VALUES
-- Sistema de Gestão de Projetos
(1, 1), -- Java
(1, 2), -- Kotlin
(1, 3), -- Spring Boot
(1, 4), -- MySQL
(1, 7), -- Comunicação
(1, 8), -- Liderança
(1, 9), -- Trabalho em Equipe
-- Portal do Cliente
(2, 1), -- Java
(2, 2), -- Kotlin
(2, 3), -- Spring Boot
(2, 4), -- MySQL
(2, 7), -- Comunicação
(2, 9), -- Trabalho em Equipe
(2, 10), -- Resolução de Problemas
-- App Mobile de Vendas
(3, 1), -- Java
(3, 2), -- Kotlin
(3, 5), -- Docker
(3, 6), -- Git
(3, 7), -- Comunicação
(3, 8), -- Liderança
(3, 11), -- Gestão de Tempo
-- Migração de Dados
(4, 4), -- MySQL
(4, 5), -- Docker
(4, 6), -- Git
(4, 10), -- Resolução de Problemas
(4, 12), -- Adaptabilidade
-- Dashboard Analytics
(5, 1), -- Java
(5, 3), -- Spring Boot
(5, 4), -- MySQL
(5, 7), -- Comunicação
(5, 10); -- Resolução de Problemas

-- Verificar os dados inseridos
SELECT 'Skills cadastradas:' as info;
SELECT id, name, type, active FROM skill ORDER BY type, name;

SELECT 'Projetos cadastrados:' as info;
SELECT id, name, status, budget, area FROM project ORDER BY status, name;

SELECT 'Relacionamentos projeto-skill:' as info;
SELECT p.name as projeto, s.name as skill, s.type as tipo_skill
FROM project p
JOIN project_skills ps ON p.id = ps.project_id
JOIN skill s ON ps.skill_id = s.id
ORDER BY p.name, s.name;