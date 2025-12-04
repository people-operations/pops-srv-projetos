package pops.application.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectResponse(
    val id: Long?,
    val name: String,
    val type: ProjectTypeResponse?,
    val description: String?,
    val status: ProjectStatusResponse,
    val budget: BigDecimal?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val area: String?,
    val active: Boolean,
    val requiredSkills: List<SkillResponse>,
    val squads: List<SquadDTO> = emptyList()
)


