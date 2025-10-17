package pops.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ProjectStatusCreateRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    val name: String,
    
    @field:Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    val description: String?
)

data class ProjectStatusUpdateRequest(
    @field:Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    val name: String?,
    
    @field:Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    val description: String?
)

data class ProjectStatusResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val active: Boolean
)




