package pops.domain.service

import org.slf4j.LoggerFactory
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
import pops.infraestructure.client.EmployeeApiClient
import pops.application.dto.ProjectTeamDetailDTO
import pops.application.dto.TeamDetailDTO
import pops.application.dto.MemberDetailDTO
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val skillRepository: SkillRepository,
    private val projectTypeRepository: ProjectTypeRepository,
    private val projectStatusRepository: ProjectStatusRepository,
    private val squadApiClient: SquadApiClient,
    private val employeeApiClient: EmployeeApiClient
) {
    
    private val logger = LoggerFactory.getLogger(ProjectService::class.java)
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
    
    /**
     * Sincroniza as skills do projeto com as skills dos colaboradores alocados nas squads
     * Busca todas as skills dos colaboradores e atualiza a tabela project_skills
     * 
     * @param projectId ID do projeto
     * @param authToken Token de autenticação (opcional)
     * @param replaceExisting Se true, substitui todas as skills existentes. Se false, adiciona às existentes (padrão: false)
     * @return Projeto atualizado
     */
    @Transactional
    fun syncProjectSkillsFromSquads(projectId: Long, authToken: String? = null, replaceExisting: Boolean = false): Project {
        logger.info("Iniciando sincronização de skills do projeto $projectId (replaceExisting: $replaceExisting)")
        val project = findById(projectId)
        
        // Buscar todas as skills únicas dos colaboradores das squads
        val allSkillNames = squadApiClient.getAllProjectSkillsNames(projectId, authToken)
        logger.info("Encontradas ${allSkillNames.size} skills únicas dos colaboradores: $allSkillNames")
        
        if (allSkillNames.isEmpty()) {
            logger.warn("Nenhuma skill encontrada nos colaboradores das squads do projeto $projectId")
            if (replaceExisting) {
                // Se replaceExisting é true e não há skills, limpar todas as skills
                project.requiredSkills.clear()
                return projectRepository.save(project)
            }
            return project
        }
        
        // Buscar as skills correspondentes no banco pelo nome
        val skillsFromSquads = allSkillNames.mapNotNull { skillName ->
            val skill = skillRepository.findByNameIgnoreCaseAndActiveTrue(skillName)
            if (skill == null) {
                logger.warn("Skill '$skillName' encontrada nos colaboradores mas não existe no banco de dados do project-manager-api")
            }
            skill
        }.toMutableSet()
        
        logger.info("Encontradas ${skillsFromSquads.size} skills correspondentes no banco de dados (de ${allSkillNames.size} skills dos colaboradores)")
        
        if (skillsFromSquads.isEmpty()) {
            logger.warn("Nenhuma skill dos colaboradores foi encontrada no banco de dados do project-manager-api")
            return project
        }
        
        // Atualizar o projeto com as skills
        // IMPORTANTE: Modificar diretamente o MutableSet para que o JPA detecte a mudança
        if (replaceExisting) {
            // Substituir todas as skills existentes pelas skills dos colaboradores
            logger.info("Substituindo todas as skills existentes (${project.requiredSkills.size}) pelas skills dos colaboradores (${skillsFromSquads.size})")
            project.requiredSkills.clear()
            project.requiredSkills.addAll(skillsFromSquads)
        } else {
            // Adicionar as novas skills mantendo as existentes
            val beforeCount = project.requiredSkills.size
            project.requiredSkills.addAll(skillsFromSquads)
            val afterCount = project.requiredSkills.size
            logger.info("Adicionando skills dos colaboradores. Antes: $beforeCount, Depois: $afterCount (${afterCount - beforeCount} novas)")
        }
        
        // Salvar o projeto (o JPA detectará as mudanças no MutableSet)
        val savedProject = projectRepository.save(project)
        
        // Forçar flush para garantir que as mudanças sejam persistidas
        projectRepository.flush()
        
        logger.info("Skills do projeto $projectId sincronizadas com sucesso. Total de skills: ${savedProject.requiredSkills.size}")
        logger.info("IDs das skills salvas: ${savedProject.requiredSkills.map { it.id }}")
        
        return savedProject
    }
    
    /**
     * Lista todos os membros dos teams do projeto com suas skills
     * Útil para diagnóstico antes de sincronizar as skills
     */
    fun listProjectTeamMembers(projectId: Long, authToken: String? = null): Map<String, Any> {
        return try {
            logger.info("Listando membros dos teams do projeto $projectId")
            val project = findById(projectId)
            
            // Buscar todas as squads do projeto
            val squads = try {
                squadApiClient.findTeamsByProjectId(projectId, authToken)
            } catch (e: Exception) {
                logger.error("Erro ao buscar squads do projeto $projectId: ${e.message}", e)
                emptyList()
            }
        
            val teamsInfo = squads.map { squad ->
                try {
                    val membersWithSkills = (squad.members ?: emptyList()).map { member ->
                        try {
                            // Buscar o employee pelo ID do member (que é o personId/fk_person)
                            val employee = try {
                                employeeApiClient.findEmployeeById(member.id, authToken)
                            } catch (e: Exception) {
                                logger.warn("Erro ao buscar employee ${member.id}: ${e.message}")
                                null
                            }
                            
                            // Determinar o nome do membro
                            val memberName = when {
                                employee != null && employee.name.isNotBlank() -> {
                                    // Employee encontrado na API, usar o nome dele (mais confiável)
                                    logger.debug("Employee ${member.id} encontrado: ${employee.name}")
                                    employee.name
                                }
                                member.name != "N/A" && member.name.isNotBlank() -> {
                                    // Employee não encontrado na API mas temos o nome do member (da allocation)
                                    logger.debug("Usando nome do member (allocation): ${member.name}")
                                    member.name
                                }
                                else -> {
                                    // Não encontrou employee e não temos nome do member
                                    logger.warn("Não foi possível obter o nome do colaborador ${member.id} - employee não encontrado e nome do member é 'N/A'")
                                    "N/A"
                                }
                            }
                            
                            mapOf(
                                "id" to member.id,
                                "name" to memberName,
                                "position" to (member.position ?: "-"),
                                "allocatedHours" to (member.allocatedHours ?: 0),
                                "skills" to (employee?.skills?.mapNotNull { skill ->
                                    try {
                                        mapOf(
                                            "id" to skill.id,
                                            "name" to (skill.name ?: "N/A"),
                                            "skillType" to (skill.skillType?.name ?: "N/A")
                                        )
                                    } catch (e: Exception) {
                                        logger.warn("Erro ao processar skill: ${e.message}")
                                        null
                                    }
                                } ?: emptyList<Map<String, Any>>()),
                                "skillsCount" to (employee?.skills?.size ?: 0),
                                "employeeFound" to (employee != null)
                            )
                        } catch (e: Exception) {
                            logger.error("Erro ao processar membro ${member.id}: ${e.message}", e)
                            // Em caso de erro, tentar usar o nome do member se disponível
                            val memberName = if (member.name != "N/A" && member.name.isNotBlank()) {
                                member.name
                            } else {
                                "N/A"
                            }
                            mapOf(
                                "id" to member.id,
                                "name" to memberName,
                                "position" to (member.position ?: "-"),
                                "allocatedHours" to (member.allocatedHours ?: 0),
                                "skills" to emptyList<Map<String, Any>>(),
                                "skillsCount" to 0,
                                "employeeFound" to false,
                                "error" to (e.message ?: "Erro desconhecido")
                            )
                        }
                    }
                    
                    mapOf(
                        "teamId" to squad.id,
                        "teamName" to (squad.name ?: "-"),
                        "teamDescription" to (squad.description ?: null),
                        "po" to (squad.po ?: null),
                        "membersCount" to (squad.members?.size ?: 0),
                        "members" to membersWithSkills
                    )
                } catch (e: Exception) {
                    logger.error("Erro ao processar squad ${squad.id}: ${e.message}", e)
                    mapOf(
                        "teamId" to squad.id,
                        "teamName" to (squad.name ?: "-"),
                        "teamDescription" to null,
                        "po" to null,
                        "membersCount" to 0,
                        "members" to emptyList<Map<String, Any>>(),
                        "error" to (e.message ?: "Erro ao processar squad")
                    )
                }
            }
        
            // Agregar todas as skills únicas encontradas
            val allSkillsFromMembers = mutableSetOf<String>()
            teamsInfo.forEach { team ->
                try {
                    (team["members"] as? List<Map<String, Any>>)?.forEach { member ->
                        try {
                            (member["skills"] as? List<Map<String, Any>>)?.forEach { skill ->
                                try {
                                    (skill["name"] as? String)?.let { allSkillsFromMembers.add(it) }
                                } catch (e: Exception) {
                                    logger.warn("Erro ao processar skill: ${e.message}")
                                }
                            }
                        } catch (e: Exception) {
                            logger.warn("Erro ao processar member: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("Erro ao processar team: ${e.message}")
                }
            }
            
            // Verificar quais skills existem no banco
            val skillsInDatabase = allSkillsFromMembers.mapNotNull { skillName ->
                try {
                    val skill = skillRepository.findByNameIgnoreCaseAndActiveTrue(skillName)
                    if (skill != null) {
                        mapOf("name" to skillName, "existsInDatabase" to true, "skillId" to skill.id)
                    } else {
                        mapOf("name" to skillName, "existsInDatabase" to false, "skillId" to null)
                    }
                } catch (e: Exception) {
                    logger.warn("Erro ao verificar skill $skillName no banco: ${e.message}")
                    null
                }
            }
            
            mapOf(
                "projectId" to projectId,
                "projectName" to (project.name ?: "-"),
                "teamsCount" to squads.size,
                "teams" to teamsInfo,
                "summary" to mapOf(
                    "totalMembers" to (squads.sumOf { it.members?.size ?: 0 }),
                    "totalUniqueSkills" to allSkillsFromMembers.size,
                    "skillsInDatabase" to skillsInDatabase.count { it["existsInDatabase"] == true },
                    "skillsNotInDatabase" to skillsInDatabase.count { it["existsInDatabase"] == false }
                ),
                "allSkills" to skillsInDatabase
            )
        } catch (e: Exception) {
            logger.error("Erro ao listar membros dos teams do projeto $projectId: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Retorna informações detalhadas dos teams do projeto incluindo cálculos financeiros
     * Inclui: nome do team, membros, skills, valor por hora e valor investido
     */
    fun getProjectTeamDetails(projectId: Long, authToken: String? = null): ProjectTeamDetailDTO {
        logger.info("Buscando detalhes dos teams do projeto $projectId")
        val project = findById(projectId)
        
        // Buscar todas as squads do projeto
        val squads = try {
            squadApiClient.findTeamsByProjectId(projectId, authToken)
        } catch (e: Exception) {
            logger.error("Erro ao buscar squads do projeto $projectId: ${e.message}", e)
            emptyList()
        }
        
        val teamsDetail = squads.map { squad ->
            val membersDetail = squad.members.mapNotNull { member ->
                try {
                    // Buscar employee completo para obter salário e horas semanais
                    val employee = employeeApiClient.findEmployeeById(member.id, authToken)
                    
                    if (employee == null) {
                        return@mapNotNull null
                    }
                    
                    // Calcular valor por hora
                    // Se o employee tem workHoursPerWeek, usar isso, senão usar 40 como padrão
                    val weeklyHours = employee.workHoursPerWeek ?: 40
                    // Cálculo: horas semanais × 4 (4 semanas por mês)
                    // Exemplo: 40h/semana × 4 = 160h/mês
                    val monthlyHoursDecimal = BigDecimal(weeklyHours * 4).setScale(2, RoundingMode.HALF_UP)
                    
                    val contractWageDecimal = employee.contractWage?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP) }
                    
                    val hourlyRate = if (contractWageDecimal != null && monthlyHoursDecimal > BigDecimal.ZERO) {
                        contractWageDecimal
                            .divide(monthlyHoursDecimal, 2, RoundingMode.HALF_UP)
                    } else {
                        null
                    }
                    
                    // Calcular valor investido no projeto
                    // hourlyRate * allocatedHours
                    val investedValue = if (hourlyRate != null && member.allocatedHours > 0) {
                        hourlyRate.multiply(BigDecimal(member.allocatedHours))
                            .setScale(2, RoundingMode.HALF_UP)
                    } else {
                        null
                    }
                    
                    MemberDetailDTO(
                        id = member.id,
                        name = employee.name,
                        jobTitle = employee.jobTitle, // Usar jobTitle da API de employees
                        allocatedHours = member.allocatedHours,
                        skills = member.skills,
                        contractWage = contractWageDecimal,
                        workHoursPerWeek = employee.workHoursPerWeek,
                        monthlyHours = monthlyHoursDecimal,
                        hourlyRate = hourlyRate,
                        investedValue = investedValue
                    )
                } catch (e: Exception) {
                    logger.error("Erro ao processar membro ${member.id}: ${e.message}", e)
                    null
                }
            }
            
            // Calcular valor total investido no team
            val totalInvestedValue = membersDetail
                .mapNotNull { it.investedValue }
                .fold(BigDecimal.ZERO) { acc, value -> acc.add(value) }
                .setScale(2, RoundingMode.HALF_UP)
            
            TeamDetailDTO(
                teamId = squad.id,
                teamName = squad.name,
                teamDescription = squad.description,
                po = squad.po,
                membersCount = membersDetail.size,
                members = membersDetail,
                totalInvestedValue = totalInvestedValue
            )
        }
        
        return ProjectTeamDetailDTO(
            projectId = projectId,
            projectName = project.name,
            teams = teamsDetail
        )
    }
}

