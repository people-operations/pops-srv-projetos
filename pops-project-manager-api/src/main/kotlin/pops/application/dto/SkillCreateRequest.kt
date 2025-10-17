package pops.application.dto

data class SkillCreateRequest(
    val name: String,
    val description: String?,
    val typeId: Long
)


