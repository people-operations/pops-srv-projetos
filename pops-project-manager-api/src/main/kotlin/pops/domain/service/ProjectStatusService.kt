package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.ProjectStatusEntity
import pops.domain.repository.ProjectStatusRepository
import pops.domain.repository.ProjectRepository
import pops.infraestructure.utilities.CrudService
import pops.application.dto.ProjectStatusUpdateRequest

@Service
class ProjectStatusService(
    private val projectStatusRepository: ProjectStatusRepository,
    private val projectRepository: ProjectRepository
) {
    
    private val crudService = CrudService(projectStatusRepository)

    init {
        println("✅ ProjectStatusService inicializado com repository = $projectStatusRepository")
    }
    
    fun findActivestatus(): List<ProjectStatusEntity> = projectStatusRepository.findByActiveTrue()
    
    fun findInactivestatus(): List<ProjectStatusEntity> = projectStatusRepository.findByActiveFalse()
    
    fun save(status: ProjectStatusEntity): ProjectStatusEntity {
        require(status.name.isNotBlank()) { "O nome do status não pode ser vazio" }
        
        if (projectStatusRepository.existsByName(status.name)) {
            throw IllegalArgumentException("Já existe um status com o nome informado")
        }
        
        return crudService.save(status)
    }
    
    fun findById(id: Long): ProjectStatusEntity = crudService.findById(id)
    
    fun findAll(): List<ProjectStatusEntity> = crudService.findAll()
    
    @Transactional
    fun disable(id: Long): ProjectStatusEntity {
        val status = findById(id)
        val disabledStatus = status.copy(active = false)
        return projectStatusRepository.save(disabledStatus)
    }
    
    @Transactional
    fun enable(id: Long): ProjectStatusEntity {
        val status = findById(id)
        val enabledStatus = status.copy(active = true)
        return projectStatusRepository.save(enabledStatus)
    }
    
    @Transactional
    fun updatePartial(id: Long, statusUpdate: ProjectStatusUpdateRequest): ProjectStatusEntity {
        val existingStatus = findById(id)
        
        // Validar nome se fornecido
        if (statusUpdate.name != null) {
            require(statusUpdate.name.isNotBlank()) { "O nome do status não pode ser vazio" }
            
            // Verificar se o nome já existe em outro status
            if (statusUpdate.name != existingStatus.name && projectStatusRepository.existsByName(statusUpdate.name)) {
                throw IllegalArgumentException("Já existe um status com o nome informado")
            }
        }
        
        // Criar status atualizado com apenas os campos fornecidos
        val updatedStatus = existingStatus.copy(
            name = statusUpdate.name ?: existingStatus.name,
            description = statusUpdate.description ?: existingStatus.description
        )
        
        return projectStatusRepository.save(updatedStatus)
    }
    
    @Transactional
    fun deleteStatus(id: Long) {
        val status = findById(id)
        
        // Verificar se o status está vinculado a algum projeto
        val projectsUsingStatus = projectRepository.findByStatus_Id(status.id!!)
        
        if (projectsUsingStatus.isNotEmpty()) {
            val projectNames = projectsUsingStatus.map { it.name }
            throw IllegalArgumentException(
                "Não é possível deletar o status '${status.name}' pois ele está vinculado aos seguintes projetos: ${projectNames.joinToString(", ")}. " +
                "Primeiro altere o status de todos os projetos antes de deletá-lo."
            )
        }
        
        // Se não há projetos vinculados, pode deletar
        projectStatusRepository.deleteById(id)
    }
}
