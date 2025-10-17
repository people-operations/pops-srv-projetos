package pops.application.dto

data class SkillResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val type: SkillTypeResponse,
    val active: Boolean
)


