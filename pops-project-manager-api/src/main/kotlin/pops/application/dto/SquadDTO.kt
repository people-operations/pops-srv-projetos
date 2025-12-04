package pops.application.dto

data class SquadDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val members: List<SquadMemberDTO> = emptyList(),
    val skills: List<SkillResponse> = emptyList(),
    val po: String? = null
)

data class SquadMemberDTO(
    val id: Long,
    val name: String,
    val position: String,
    val allocatedHours: Int
)







