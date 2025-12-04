package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.Project
import pops.domain.repository.ProjectRepository
import pops.domain.repository.SkillRepository
import pops.domain.repository.ProjectTypeRepository
import pops.domain.repository.ProjectStatusRepository
import pops.infraestructure.utilities.CrudService
import pops.application.dto.ProjectUpdateRequest
import pops.application.dto.ProjectResponse
import pops.application.dto.ProjectTypeResponse
import pops.application.dto.ProjectStatusResponse
import pops.application.dto.SkillResponse
import pops.application.dto.SkillTypeResponse
import pops.infraestructure.client.SquadApiClient
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val skillRepository: SkillRepository,
    private val projectTypeRepository: ProjectTypeRepository,
    private val projectStatusRepository: ProjectStatusRepository,
    private val squadApiClient: SquadApiClient
) {
    
    private val crudService = CrudService(projectRepository)

    init {
        println("✅ ProjectService inicializado com repository = $projectRepository")
    }
    
    fun findActiveProjects(): List<Project> = projectRepository.findByActiveTrue()
    
    fun findInactiveProjects(): List<Project> = projectRepository.findByActiveFalse()
    
    fun findActiveProjectsByStatus(statusId: Long): List<Project> = 
        projectRepository.findByActiveTrueAndProjectStatusId(statusId)
    
    fun save(project: Project): Project {
        require(project.name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
        
        if (projectRepository.existsByName(project.name)) {
            throw IllegalArgumentException("Já existe um projeto com o nome informado")
        }
        
        // Validar skills se fornecidas
        if (project.requiredSkills.isNotEmpty()) {
            val skillIds = project.requiredSkills.mapNotNull { it.id }
            val existingSkills = skillRepository.findAllById(skillIds)
            if (existingSkills.size != skillIds.size) {
                throw IllegalArgumentException("Uma ou mais skills informadas não existem")
            }
        }
        
        return crudService.save(project)
    }
    
    fun update(id: Long, project: Project): Project {
        require(project.name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
        
        val existingProject = findById(id)
        
        // Verificar se o nome já existe em outro projeto
        if (project.name != existingProject.name && projectRepository.existsByName(project.name)) {
            throw IllegalArgumentException("Já existe um projeto com o nome informado")
        }
        
        // Validar skills se fornecidas
        if (project.requiredSkills.isNotEmpty()) {
            val skillIds = project.requiredSkills.mapNotNull { it.id }
            val existingSkills = skillRepository.findAllById(skillIds)
            if (existingSkills.size != skillIds.size) {
                throw IllegalArgumentException("Uma ou mais skills informadas não existem")
            }
        }
        
        return crudService.update(id, project)
    }
    
    fun findById(id: Long): Project = crudService.findById(id)
    
    fun findByIdWithSquads(id: Long, authToken: String? = null): ProjectResponse {
        val project = crudService.findById(id)
        return toProjectResponse(project, authToken)
    }
    
    fun findAll(): List<Project> = crudService.findAll()
    
    fun findAllWithSquads(authToken: String? = null): List<ProjectResponse> {
        val projects = crudService.findAll()
        return projects.map { toProjectResponse(it, authToken) }
    }
    
    fun findActiveProjectsWithSquads(authToken: String? = null): List<ProjectResponse> {
        val projects = findActiveProjects()
        return projects.map { toProjectResponse(it, authToken) }
    }
    
    fun findInactiveProjectsWithSquads(authToken: String? = null): List<ProjectResponse> {
        val projects = findInactiveProjects()
        return projects.map { toProjectResponse(it, authToken) }
    }
    
    fun findActiveProjectsByStatusWithSquads(statusId: Long, authToken: String? = null): List<ProjectResponse> {
        val projects = findActiveProjectsByStatus(statusId)
        return projects.map { toProjectResponse(it, authToken) }
    }
    
    fun findAllPageableWithSquads(pageable: org.springframework.data.domain.Pageable, authToken: String? = null): org.springframework.data.domain.Page<ProjectResponse> {
        val projectsPage = crudService.findAllPageable(pageable)
        return projectsPage.map { toProjectResponse(it, authToken) }
    }
    
    private fun toProjectResponse(project: Project, authToken: String? = null): ProjectResponse {
        val squads = project.id?.let { 
            squadApiClient.findTeamsByProjectId(it, authToken) 
        } ?: emptyList()
        
        return ProjectResponse(
            id = project.id,
            name = project.name,
            type = project.type?.let {
                ProjectTypeResponse(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    active = it.active
                )
            },
            description = project.description,
            status = ProjectStatusResponse(
                id = project.status.id,
                name = project.status.name,
                description = project.status.description,
                active = project.status.active
            ),
            budget = project.budget,
            startDate = project.startDate,
            endDate = project.endDate,
            area = project.area,
            active = project.active,
            requiredSkills = project.requiredSkills.map { skill ->
                SkillResponse(
                    id = skill.id,
                    name = skill.name,
                    description = skill.description,
                    type = SkillTypeResponse(
                        id = skill.type.id,
                        name = skill.type.name,
                        description = skill.type.description,
                        active = skill.type.active
                    ),
                    active = skill.active
                )
            },
            squads = squads
        )
    }
    
    fun findAllPageable(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<Project> = crudService.findAllPageable(pageable)
    
    @Transactional
    fun disable(id: Long): Project {
        val project = findById(id)
        val disabledProject = project.copy(active = false)
        return projectRepository.save(disabledProject)
    }
    
    @Transactional
    fun enable(id: Long): Project {
        val project = findById(id)
        val enabledProject = project.copy(active = true)
        return projectRepository.save(enabledProject)
    }
    
    @Transactional
    fun saveWithSkills(
        name: String,
        typeId: Long?,
        description: String?,
        statusId: Long,
        budget: BigDecimal?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        area: String?,
        skillIds: List<Long>
    ): Project {
        require(name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
        
        if (projectRepository.existsByName(name)) {
            throw IllegalArgumentException("Já existe um projeto com o nome informado")
        }
        
        // Buscar tipo se fornecido
        val projectType = typeId?.let { 
            projectTypeRepository.findById(it).orElseThrow { 
                IllegalArgumentException("Tipo de projeto não encontrado com ID: $it") 
            }
        }
        
        // Buscar status
        val projectStatus = projectStatusRepository.findById(statusId).orElseThrow { 
            IllegalArgumentException("Status de projeto não encontrado com ID: $statusId") 
        }
        
        // Buscar skills pelos IDs fornecidos
        val skills = if (skillIds.isNotEmpty()) {
            val existingSkills = skillRepository.findAllById(skillIds)
            if (existingSkills.size != skillIds.size) {
                throw IllegalArgumentException("Uma ou mais skills informadas não existem")
            }
            existingSkills.toMutableSet()
        } else {
            mutableSetOf()
        }
        
        val project = Project(
            name = name,
            type = projectType,
            description = description,
            status = projectStatus,
            budget = budget,
            startDate = startDate,
            endDate = endDate,
            area = area,
            requiredSkills = skills
        )
        
        return projectRepository.save(project)
    }
    
    @Transactional
    fun updatePartial(id: Long, projectUpdate: ProjectUpdateRequest): Project {
        val existingProject = findById(id)
        
        // Validar nome se fornecido
        if (projectUpdate.name != null) {
            require(projectUpdate.name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
            
            // Verificar se o nome já existe em outro projeto
            if (projectUpdate.name != existingProject.name && projectRepository.existsByName(projectUpdate.name)) {
                throw IllegalArgumentException("Já existe um projeto com o nome informado")
            }
        }
        
        // Buscar tipo se fornecido
        val projectType = projectUpdate.typeId?.let { 
            projectTypeRepository.findById(it).orElseThrow { 
                IllegalArgumentException("Tipo de projeto não encontrado com ID: $it") 
            }
        } ?: existingProject.type
        
        // Buscar status se fornecido
        val projectStatus = projectUpdate.statusId?.let { 
            projectStatusRepository.findById(it).orElseThrow { 
                IllegalArgumentException("Status de projeto não encontrado com ID: $it") 
            }
        } ?: existingProject.status
        
        // Validar skills se fornecidas
        val skills = if (projectUpdate.skillIds != null) {
            if (projectUpdate.skillIds.isNotEmpty()) {
                val existingSkills = skillRepository.findAllById(projectUpdate.skillIds)
                if (existingSkills.size != projectUpdate.skillIds.size) {
                    throw IllegalArgumentException("Uma ou mais skills informadas não existem")
                }
                existingSkills.toMutableSet()
            } else {
                mutableSetOf()
            }
        } else {
            existingProject.requiredSkills
        }
        
        // Criar projeto atualizado com apenas os campos fornecidos
        val updatedProject = existingProject.copy(
            name = projectUpdate.name ?: existingProject.name,
            type = projectType,
            description = projectUpdate.description ?: existingProject.description,
            status = projectStatus,
            budget = projectUpdate.budget ?: existingProject.budget,
            startDate = projectUpdate.startDate ?: existingProject.startDate,
            endDate = projectUpdate.endDate ?: existingProject.endDate,
            area = projectUpdate.area ?: existingProject.area,
            requiredSkills = skills
        )
        
        return projectRepository.save(updatedProject)
    }
    
    @Transactional
    fun deleteProject(id: Long) {
        val project = findById(id)
        
        // Remover todas as associações com skills antes de deletar o projeto
        if (project.requiredSkills.isNotEmpty()) {
            val projectWithoutSkills = project.copy(requiredSkills = mutableSetOf())
            projectRepository.save(projectWithoutSkills)
        }
        
        // Agora deletar o projeto
        projectRepository.deleteById(id)
    }
}

