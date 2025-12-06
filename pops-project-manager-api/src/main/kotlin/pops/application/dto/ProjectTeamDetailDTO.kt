package pops.application.dto

import java.math.BigDecimal

/**
 * DTO específico com informações detalhadas dos teams do projeto
 * Inclui membros, skills e cálculos financeiros
 */
data class ProjectTeamDetailDTO(
    val projectId: Long,
    val projectName: String,
    val teams: List<TeamDetailDTO>
)

data class TeamDetailDTO(
    val teamId: Long,
    val teamName: String,
    val teamDescription: String?,
    val po: String?,
    val membersCount: Int,
    val members: List<MemberDetailDTO>,
    val totalInvestedValue: BigDecimal
)

data class MemberDetailDTO(
    val id: Long,
    val name: String,
    val jobTitle: String?, // Cargo do colaborador (vindo da API de employees)
    val allocatedHours: Int,
    val skills: List<MemberSkillDTO>,
    val contractWage: BigDecimal?, // Salário mensal
    val workHoursPerWeek: Int?, // Horas semanais de trabalho
    val monthlyHours: BigDecimal?, // Horas mensais calculadas (workHoursPerWeek * 4.33)
    val hourlyRate: BigDecimal?, // Valor por hora (salário/horas mensais)
    val investedValue: BigDecimal? // Valor investido no projeto (hourlyRate * allocatedHours)
)

