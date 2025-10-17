package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.SkillTypeEntity
import pops.domain.repository.SkillTypeRepository
import pops.domain.repository.SkillRepository
import pops.infraestructure.utilities.CrudService
import pops.application.dto.SkillTypeUpdateRequest

@Service
class SkillTypeService(
    private val skillTypeRepository: SkillTypeRepository,
    private val skillRepository: SkillRepository
) {
    
    private val crudService = CrudService(skillTypeRepository)

    init {
        println("✅ SkillTypeService inicializado com repository = $skillTypeRepository")
    }
    
    fun findActiveTypes(): List<SkillTypeEntity> = skillTypeRepository.findByActiveTrue()
    
    fun findInactiveTypes(): List<SkillTypeEntity> = skillTypeRepository.findByActiveFalse()
    
    fun save(type: SkillTypeEntity): SkillTypeEntity {
        require(type.name.isNotBlank()) { "O nome do tipo não pode ser vazio" }
        
        if (skillTypeRepository.existsByName(type.name)) {
            throw IllegalArgumentException("Já existe um tipo com o nome informado")
        }
        
        return crudService.save(type)
    }
    
    fun findById(id: Long): SkillTypeEntity = crudService.findById(id)
    
    fun findAll(): List<SkillTypeEntity> = crudService.findAll()
    
    @Transactional
    fun disable(id: Long): SkillTypeEntity {
        val type = findById(id)
        val disabledType = type.copy(active = false)
        return skillTypeRepository.save(disabledType)
    }
    
    @Transactional
    fun enable(id: Long): SkillTypeEntity {
        val type = findById(id)
        val enabledType = type.copy(active = true)
        return skillTypeRepository.save(enabledType)
    }
    
    @Transactional
    fun updatePartial(id: Long, typeUpdate: SkillTypeUpdateRequest): SkillTypeEntity {
        val existingType = findById(id)
        
        // Validar nome se fornecido
        if (typeUpdate.name != null) {
            require(typeUpdate.name.isNotBlank()) { "O nome do tipo não pode ser vazio" }
            
            // Verificar se o nome já existe em outro tipo
            if (typeUpdate.name != existingType.name && skillTypeRepository.existsByName(typeUpdate.name)) {
                throw IllegalArgumentException("Já existe um tipo com o nome informado")
            }
        }
        
        // Criar tipo atualizado com apenas os campos fornecidos
        val updatedType = existingType.copy(
            name = typeUpdate.name ?: existingType.name,
            description = typeUpdate.description ?: existingType.description
        )
        
        return skillTypeRepository.save(updatedType)
    }
    
    @Transactional
    fun deleteType(id: Long) {
        val type = findById(id)
        
        // Verificar se o tipo está vinculado a alguma skill
        val skillsUsingType = skillRepository.findByType_Id(type.id!!)
        
        if (skillsUsingType.isNotEmpty()) {
            val skillNames = skillsUsingType.map { it.name }
            throw IllegalArgumentException(
                "Não é possível deletar o tipo '${type.name}' pois ele está vinculado às seguintes skills: ${skillNames.joinToString(", ")}. " +
                "Primeiro altere o tipo de todas as skills antes de deletá-lo."
            )
        }
        
        // Se não há skills vinculadas, pode deletar
        skillTypeRepository.deleteById(id)
    }
}
