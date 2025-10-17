package pops.application.dto

import pops.domain.model.enum.SkillType
import pops.domain.model.enum.ProjectStatus
import java.math.BigDecimal
import java.time.LocalDate

// Exemplos de dados para testar a API

// Skills de exemplo
val exampleSkills = listOf(
    mapOf(
        "name" to "Java",
        "description" to "Linguagem de programação Java",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Kotlin",
        "description" to "Linguagem de programação Kotlin",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Spring Boot",
        "description" to "Framework Spring Boot para desenvolvimento de APIs",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Comunicação",
        "description" to "Habilidade de comunicação interpessoal",
        "type" to SkillType.SOFT.name
    ),
    mapOf(
        "name" to "Liderança",
        "description" to "Capacidade de liderar equipes",
        "type" to SkillType.SOFT.name
    )
)

// Projetos de exemplo (agora com skillIds em vez de objetos completos)
val exampleProjects = listOf(
    mapOf(
        "name" to "Sistema de Gestão de Projetos",
        "type" to "Desenvolvimento",
        "description" to "Sistema para gerenciar projetos e equipes",
        "status" to ProjectStatus.IN_PROGRESS.name,
        "budget" to BigDecimal("50000.00"),
        "startDate" to LocalDate.now().minusDays(30),
        "endDate" to LocalDate.now().plusDays(60),
        "area" to "Tecnologia",
        "skillIds" to listOf(1L, 2L, 3L) // IDs das skills necessárias
    ),
    mapOf(
        "name" to "Portal do Cliente",
        "type" to "Desenvolvimento Web",
        "description" to "Portal web para clientes acessarem serviços",
        "status" to ProjectStatus.PLANNING.name,
        "budget" to BigDecimal("75000.00"),
        "startDate" to LocalDate.now().plusDays(15),
        "endDate" to LocalDate.now().plusDays(90),
        "area" to "Tecnologia",
        "skillIds" to listOf(1L, 3L, 4L) // IDs das skills necessárias
    )
)

// Exemplos de atualizações parciais de projetos (PATCH)
val exampleProjectUpdates = listOf(
    mapOf(
        "name" to "Sistema de Gestão de Projetos v2.0",
        "status" to ProjectStatus.COMPLETED.name,
        "budget" to BigDecimal("55000.00")
    ),
    mapOf(
        "description" to "Portal web modernizado para clientes",
        "area" to "Tecnologia e Inovação",
        "skillIds" to listOf(1L, 2L, 3L, 4L)
    ),
    mapOf(
        "status" to ProjectStatus.IN_PROGRESS.name,
        "startDate" to LocalDate.now(),
        "endDate" to LocalDate.now().plusDays(30)
    )
)

// Projetos desativados de exemplo
val exampleInactiveProjects = listOf(
    mapOf(
        "name" to "Sistema Legado",
        "type" to "Manutenção",
        "description" to "Sistema antigo que foi descontinuado",
        "status" to ProjectStatus.CANCELLED.name,
        "budget" to BigDecimal("25000.00"),
        "startDate" to LocalDate.now().minusDays(365),
        "endDate" to LocalDate.now().minusDays(300),
        "area" to "Legado",
        "skillIds" to listOf(1L, 2L) // IDs das skills que eram necessárias
    ),
    mapOf(
        "name" to "Projeto Piloto",
        "type" to "Pesquisa",
        "description" to "Projeto piloto que não foi aprovado",
        "status" to ProjectStatus.CANCELLED.name,
        "budget" to BigDecimal("15000.00"),
        "startDate" to LocalDate.now().minusDays(180),
        "endDate" to LocalDate.now().minusDays(120),
        "area" to "Pesquisa e Desenvolvimento",
        "skillIds" to listOf(3L, 4L, 5L) // IDs das skills que eram necessárias
    )
)


