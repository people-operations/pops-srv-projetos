package pops.application.dto

data class SkillUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val typeId: Long? = null
)


