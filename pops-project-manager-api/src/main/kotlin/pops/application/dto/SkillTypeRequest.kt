package pops.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SkillTypeCreateRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    val name: String,
    
    @field:Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    val description: String?
)

data class SkillTypeUpdateRequest(
    @field:Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    val name: String?,
    
    @field:Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    val description: String?
)

data class SkillTypeResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val active: Boolean
)




