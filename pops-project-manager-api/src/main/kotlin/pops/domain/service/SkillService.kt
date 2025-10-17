package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.Skill
import pops.domain.repository.SkillRepository
import pops.domain.repository.ProjectRepository
import pops.domain.repository.SkillTypeRepository
import pops.infraestructure.utilities.CrudService
import pops.application.dto.SkillUpdateRequest

@Service
class SkillService(
    private val skillRepository: SkillRepository,
    private val projectRepository: ProjectRepository,
    private val skillTypeRepository: SkillTypeRepository
) {
    
    private val crudService = CrudService(skillRepository)

    init {
        println("✅ SkillService inicializado com repository = $skillRepository")
    }
    
    fun findActiveSkills(): List<Skill> = skillRepository.findByActiveTrue()
    
    fun findInactiveSkills(): List<Skill> = skillRepository.findByActiveFalse()
    
    fun findActiveSkillsByType(typeId: Long): List<Skill> = 
        skillRepository.findByActiveTrueAndSkillTypeId(typeId)
    
    fun save(skill: Skill): Skill {
        require(skill.name.isNotBlank()) { "O nome da skill não pode ser vazio" }
        
        if (skillRepository.existsByName(skill.name)) {
            throw IllegalArgumentException("Já existe uma skill com o nome informado")
        }
        
        return crudService.save(skill)
    }
    
    @Transactional
    fun saveWithType(
        name: String,
        description: String?,
        typeId: Long
    ): Skill {
        require(name.isNotBlank()) { "O nome da skill não pode ser vazio" }
        
        if (skillRepository.existsByName(name)) {
            throw IllegalArgumentException("Já existe uma skill com o nome informado")
        }
        
        // Buscar tipo
        val skillType = skillTypeRepository.findById(typeId).orElseThrow { 
            IllegalArgumentException("Tipo de skill não encontrado com ID: $typeId") 
        }
        
        val skill = Skill(
            name = name,
            description = description,
            type = skillType
        )
        
        return skillRepository.save(skill)
    }
    
    fun findById(id: Long): Skill = crudService.findById(id)
    
    fun findAll(): List<Skill> = crudService.findAll()
    
    fun findAllPageable(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<Skill> = crudService.findAllPageable(pageable)
    
    @Transactional
    fun disable(id: Long): Skill {
        val skill = findById(id)
        val disabledSkill = skill.copy(active = false)
        return skillRepository.save(disabledSkill)
    }
    
    @Transactional
    fun enable(id: Long): Skill {
        val skill = findById(id)
        val enabledSkill = skill.copy(active = true)
        return skillRepository.save(enabledSkill)
    }
    
    @Transactional
    fun updatePartial(id: Long, skillUpdate: SkillUpdateRequest): Skill {
        val existingSkill = findById(id)
        
        // Validar nome se fornecido
        if (skillUpdate.name != null) {
            require(skillUpdate.name.isNotBlank()) { "O nome da skill não pode ser vazio" }
            
            // Verificar se o nome já existe em outra skill
            if (skillUpdate.name != existingSkill.name && skillRepository.existsByName(skillUpdate.name)) {
                throw IllegalArgumentException("Já existe uma skill com o nome informado")
            }
        }
        
        // Buscar tipo se fornecido
        val skillType = skillUpdate.typeId?.let { 
            skillTypeRepository.findById(it).orElseThrow { 
                IllegalArgumentException("Tipo de skill não encontrado com ID: $it") 
            }
        } ?: existingSkill.type
        
        // Criar skill atualizada com apenas os campos fornecidos
        val updatedSkill = existingSkill.copy(
            name = skillUpdate.name ?: existingSkill.name,
            description = skillUpdate.description ?: existingSkill.description,
            type = skillType
        )
        
        return skillRepository.save(updatedSkill)
    }
    
    @Transactional
    fun deleteSkill(id: Long) {
        val skill = findById(id)
        
        // Verificar se a skill está vinculada a algum projeto
        val projectsUsingSkill = projectRepository.findProjectsBySkillId(id)
        
        if (projectsUsingSkill.isNotEmpty()) {
            val projectNames = projectsUsingSkill.map { it.name }
            throw IllegalArgumentException(
                "Não é possível deletar a skill '${skill.name}' pois ela está vinculada aos seguintes projetos: ${projectNames.joinToString(", ")}. " +
                "Primeiro desvincule a skill de todos os projetos antes de deletá-la."
            )
        }
        
        // Se não há projetos vinculados, pode deletar
        skillRepository.deleteById(id)
    }
}

