-- =========================================================
-- Script completo para criação do banco e autenticação MySQL
-- Projeto: PeopleOps Project Manager API
-- =========================================================
DROP DATABASE IF EXISTS popsdb;

-- Criar banco de dados
CREATE DATABASE IF NOT EXISTS popsdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE popsdb;

-- =========================================================
-- Tabelas de domínio e referência
-- =========================================================

CREATE TABLE IF NOT EXISTS skill_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS project_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS project_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =========================================================
-- Entidades principais
-- =========================================================

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

CREATE TABLE IF NOT EXISTS project_skills (
    project_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, skill_id),
    FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skill(id) ON DELETE CASCADE
);

-- =========================================================
-- Tabelas relacionadas à gestão de equipes
-- =========================================================

CREATE TABLE IF NOT EXISTS team (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fk_approver BIGINT NULL DEFAULT NULL,
    description VARCHAR(100) NULL DEFAULT NULL,
    name VARCHAR(60) NOT NULL,
    fk_project BIGINT NOT NULL,
    sprint_duration INT NOT NULL,
    status TINYINT(1) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (fk_project) REFERENCES project(id)
);

CREATE TABLE IF NOT EXISTS allocation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    allocated_hours INT NOT NULL,
    fk_person BIGINT NOT NULL,
    position VARCHAR(255) NOT NULL,
    started_at DATE NOT NULL,
    fk_team BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_allocation_team FOREIGN KEY (fk_team) REFERENCES team (id)
);

CREATE TABLE IF NOT EXISTS allocation_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    allocated_hours INT NOT NULL,
    ended_at DATE NOT NULL,
    person_id BIGINT NOT NULL,
    position VARCHAR(255) NOT NULL,
    started_at DATE NOT NULL,
    fk_person BIGINT NOT NULL,
    fk_team BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_allocation_history_team FOREIGN KEY (fk_team) REFERENCES team (id)
);

-- =========================================================
-- Dados iniciais
-- =========================================================

INSERT INTO skill_type (name, description) VALUES
    ('HARD', 'Habilidades técnicas e conhecimentos específicos'),
    ('SOFT', 'Habilidades comportamentais e interpessoais'),
    ('MANAGEMENT', 'Habilidades de gestão e liderança'),
    ('ANALYTICS', 'Habilidades de análise de dados e métricas');

INSERT INTO project_type (name, description) VALUES
    ('DESENVOLVIMENTO', 'Projetos de desenvolvimento de software'),
    ('WEB', 'Projetos de desenvolvimento web'),
    ('MOBILE', 'Projetos de desenvolvimento mobile'),
    ('INFRAESTRUTURA', 'Projetos de infraestrutura e DevOps'),
    ('BI', 'Projetos de Business Intelligence e Analytics'),
    ('CONSULTORIA', 'Projetos de consultoria e análise');

INSERT INTO project_status (name, description) VALUES
    ('EM PLANEJAMENTO', 'Projeto em fase de planejamento'),
    ('EM PROGRESSO', 'Projeto em andamento'),
    ('EM ESPERA', 'Projeto pausado temporariamente'),
    ('CONCLUÍDO', 'Projeto concluído com sucesso'),
    ('CANCELADO', 'Projeto cancelado');

INSERT INTO skill (name, description, skill_type_id) VALUES
    ('Java', 'Linguagem de programação Java', 1),
    ('Kotlin', 'Linguagem de programação Kotlin', 1),
    ('Spring Boot', 'Framework Spring Boot para desenvolvimento de APIs', 1),
    ('MySQL', 'Sistema de gerenciamento de banco de dados MySQL', 1),
    ('Docker', 'Plataforma de containerização', 1),
    ('Git', 'Sistema de controle de versão', 1),
    ('React', 'Biblioteca JavaScript para interfaces de usuário', 1),
    ('Node.js', 'Runtime JavaScript para desenvolvimento backend', 1),
    ('Comunicação', 'Habilidade de comunicação interpessoal', 2),
    ('Liderança', 'Capacidade de liderar equipes', 2),
    ('Trabalho em Equipe', 'Habilidade de trabalhar colaborativamente', 2),
    ('Resolução de Problemas', 'Capacidade de analisar e resolver problemas', 2),
    ('Gestão de Tempo', 'Habilidade de gerenciar tempo e prioridades', 2),
    ('Adaptabilidade', 'Capacidade de se adaptar a mudanças', 2),
    ('Gestão de Projetos', 'Habilidade em gerenciar projetos', 3),
    ('Gestão de Equipes', 'Habilidade em gerenciar equipes', 3),
    ('Scrum', 'Metodologia ágil Scrum', 3),
    ('Power BI', 'Ferramenta de Business Intelligence', 4),
    ('Python', 'Linguagem de programação para análise de dados', 4),
    ('SQL Avançado', 'Consultas SQL complexas e otimização', 4);

-- ====== APENAS 5 PROJETOS ======
INSERT INTO project (name, project_type_id, description, project_status_id, budget, start_date, end_date, area) VALUES
    ( 'Sistema de Gestão de Projetos', 1,
      'Sistema completo para gerenciar projetos, equipes e recursos',
      2, 50000.00, '2024-09-01', '2024-12-31', 'Tecnologia' ),
    ( 'Portal do Cliente', 2,
      'Portal web responsivo para clientes acessarem serviços e informações',
      1, 75000.00, '2024-10-15', '2025-02-15', 'Tecnologia' ),
    ( 'App Mobile de Vendas', 3,
      'Aplicativo mobile para equipe de vendas externas',
      1, 120000.00, '2024-11-01', '2025-05-01', 'Tecnologia' ),
    ( 'Migração de Dados', 4,
      'Migração de dados legados para nova arquitetura',
      3, 30000.00, '2024-08-01', '2024-10-31', 'Infraestrutura' ),
    ( 'Dashboard Analytics', 5,
      'Dashboard para análise de métricas e KPIs',
      4, 25000.00, '2024-06-01', '2024-08-31', 'Analytics' );

-- =========================================================
-- TEAMS (1 squad por projeto 1..5)
-- fk_approver em {510, 1362, 2334}
-- =========================================================
INSERT INTO team (fk_approver, description, name, fk_project, sprint_duration, status)
VALUES
    (510,  'Equipe responsável pelo projeto 1', 'Team 1', 1, 10, 1),
    (1362, 'Equipe responsável pelo projeto 2', 'Team 2', 2, 10, 1),
    (2334, 'Equipe responsável pelo projeto 3', 'Team 3', 3, 10, 1),
    (510,  'Equipe responsável pelo projeto 4', 'Team 4', 4, 10, 1),
    (1362, 'Equipe responsável pelo projeto 5', 'Team 5', 5, 10, 1);

-- =========================================================
-- ALLOCATIONS (5 pessoas por squad, times 1..5)
-- IDs de time e projeto ficam consistentes (1..5)
-- =========================================================
INSERT INTO allocation (allocated_hours, fk_person, position, started_at, fk_team)
VALUES
    -- Team 1 (Project 1)
    (37, 2475, 'Desenvolvedor Backend',  '2024-09-08', 1),
    (45, 2505, 'Desenvolvedor Frontend', '2024-09-07', 1),
    (31, 2517, 'Dev Mobile',             '2024-09-05', 1),
    (39, 2466, 'QA Analyst',             '2024-09-11', 1),
    (37, 2512, 'Scrum Master',           '2024-09-09', 1),

    -- Team 2 (Project 2)
    (37, 1115, 'Desenvolvedor Backend',  '2024-10-18', 2),
    (40, 2483, 'Desenvolvedor Frontend', '2024-10-25', 2),
    (36, 1385, 'Dev Mobile',             '2024-10-20', 2),
    (36, 2448, 'QA Analyst',             '2024-10-15', 2),
    (38, 2510, 'Scrum Master',           '2024-10-18', 2),
    (20, 2475, 'Desenvolvedor Backend', '2024-10-01', 2),

    -- Team 3 (Project 3)
    (42, 2504, 'Desenvolvedor Backend',  '2024-11-01', 3),
    (39, 2462, 'Desenvolvedor Frontend', '2024-11-06', 3),
    (38, 2484, 'Dev Mobile',             '2024-11-07', 3),
    (38, 2432, 'QA Analyst',             '2024-11-05', 3),
    (40, 2450, 'Scrum Master',           '2024-11-02', 3),

    -- Team 4 (Project 4)
    (36, 2497, 'Desenvolvedor Backend',  '2024-08-04', 4),
    (38, 2495, 'Desenvolvedor Frontend', '2024-08-10', 4),
    (38, 2485, 'Dev Mobile',             '2024-08-06', 4),
    (40, 2481, 'QA Analyst',             '2024-08-04', 4),
    (37, 2480, 'Scrum Master',           '2024-08-11', 4),

    -- Team 5 (Project 5)
    (45, 2442, 'Desenvolvedor Backend',  '2024-06-08', 5),
    (37, 2456, 'Desenvolvedor Frontend', '2024-06-07', 5),
    (40, 2476, 'Dev Mobile',             '2024-06-11', 5),
    (18, 2472, 'QA Analyst',             '2024-06-08', 5),
    (21, 1145, 'Scrum Master',           '2024-06-03', 5);

-- Conferência
SELECT * FROM project;
SELECT * FROM team;
SELECT * FROM allocation;
