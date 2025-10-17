package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.ProjectTypeEntity
import pops.domain.repository.ProjectTypeRepository
import pops.domain.repository.ProjectRepository
import pops.infraestructure.utilities.CrudService
import pops.application.dto.ProjectTypeUpdateRequest

@Service
class ProjectTypeService(
    private val projectTypeRepository: ProjectTypeRepository,
    private val projectRepository: ProjectRepository
) {
    
    private val crudService = CrudService(projectTypeRepository)

    init {
        println("✅ ProjectTypeService inicializado com repository = $projectTypeRepository")
    }
    
    fun findActiveTypes(): List<ProjectTypeEntity> = projectTypeRepository.findByActiveTrue()
    
    fun findInactiveTypes(): List<ProjectTypeEntity> = projectTypeRepository.findByActiveFalse()
    
    fun save(type: ProjectTypeEntity): ProjectTypeEntity {
        require(type.name.isNotBlank()) { "O nome do tipo não pode ser vazio" }
        
        if (projectTypeRepository.existsByName(type.name)) {
            throw IllegalArgumentException("Já existe um tipo com o nome informado")
        }
        
        return crudService.save(type)
    }
    
    fun findById(id: Long): ProjectTypeEntity = crudService.findById(id)
    
    fun findAll(): List<ProjectTypeEntity> = crudService.findAll()
    
    @Transactional
    fun disable(id: Long): ProjectTypeEntity {
        val type = findById(id)
        val disabledType = type.copy(active = false)
        return projectTypeRepository.save(disabledType)
    }
    
    @Transactional
    fun enable(id: Long): ProjectTypeEntity {
        val type = findById(id)
        val enabledType = type.copy(active = true)
        return projectTypeRepository.save(enabledType)
    }
    
    @Transactional
    fun updatePartial(id: Long, typeUpdate: ProjectTypeUpdateRequest): ProjectTypeEntity {
        val existingType = findById(id)
        
        // Validar nome se fornecido
        if (typeUpdate.name != null) {
            require(typeUpdate.name.isNotBlank()) { "O nome do tipo não pode ser vazio" }
            
            // Verificar se o nome já existe em outro tipo
            if (typeUpdate.name != existingType.name && projectTypeRepository.existsByName(typeUpdate.name)) {
                throw IllegalArgumentException("Já existe um tipo com o nome informado")
            }
        }
        
        // Criar tipo atualizado com apenas os campos fornecidos
        val updatedType = existingType.copy(
            name = typeUpdate.name ?: existingType.name,
            description = typeUpdate.description ?: existingType.description
        )
        
        return projectTypeRepository.save(updatedType)
    }
    
    @Transactional
    fun deleteType(id: Long) {
        val type = findById(id)
        
        // Verificar se o tipo está vinculado a algum projeto
        val projectsUsingType = projectRepository.findByType_Id(type.id!!)
        
        if (projectsUsingType.isNotEmpty()) {
            val projectNames = projectsUsingType.map { it.name }
            throw IllegalArgumentException(
                "Não é possível deletar o tipo '${type.name}' pois ele está vinculado aos seguintes projetos: ${projectNames.joinToString(", ")}. " +
                "Primeiro altere o tipo de todos os projetos antes de deletá-lo."
            )
        }
        
        // Se não há projetos vinculados, pode deletar
        projectTypeRepository.deleteById(id)
    }
}
