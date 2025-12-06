package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Project
import pops.domain.service.ProjectService
import pops.application.dto.ProjectCreateRequest
import pops.application.dto.ProjectUpdateRequest
import pops.application.dto.ProjectResponse
import pops.application.dto.ProjectTeamDetailDTO
import pops.application.dto.SkillResponse
import pops.application.dto.ProjectTypeResponse
import pops.application.dto.ProjectStatusResponse
import pops.infraestructure.utilities.CrudService

@RestController
@RequestMapping(value = ["/api/projects", "/projects"])
class ProjectController(
    private val service: ProjectService,
) {

    private val logger = LoggerFactory.getLogger(ProjectController::class.java)

    @GetMapping
    fun listActiveProjects(
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        val projects = service.findActiveProjectsWithSquads(authHeader)
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/inactive")
    fun listInactiveProjects(
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        val projects = service.findInactiveProjectsWithSquads(authHeader)
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/pageable")
    fun listProjectsPagination(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        val projects = service.findAllPageableWithSquads(pageable, authHeader)
        return if (projects.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects.content)
    }

    @GetMapping("/status/{statusId}")
    fun listProjectsByStatus(
        @PathVariable statusId: Long,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        val projects = service.findActiveProjectsByStatusWithSquads(statusId, authHeader)
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(
        @PathVariable id: Long,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        logger.info("Buscando projeto com ID: $id")
        try {
            val projectResponse = service.findByIdWithSquads(id, authHeader)
            logger.info("Projeto encontrado: ${projectResponse.name}")
            return ResponseEntity.ok(projectResponse)
        } catch (e: Exception) {
            logger.error("Erro ao buscar projeto $id: ${e.message}", e)
            throw e
        }
    }

    @PostMapping
    fun createProject(@RequestBody projectRequest: ProjectCreateRequest): ResponseEntity<Project> {
        val newProject = service.saveWithSkills(
            name = projectRequest.name,
            typeId = projectRequest.typeId,
            description = projectRequest.description,
            statusId = projectRequest.statusId,
            budget = projectRequest.budget,
            startDate = projectRequest.startDate,
            endDate = projectRequest.endDate,
            area = projectRequest.area,
            skillIds = projectRequest.skillIds
        )
        logger.info("Projeto criado com sucesso: ${newProject.id}")
        return ResponseEntity.status(201).body(newProject)
    }

    @PatchMapping("/{id}")
    fun updateProject(@PathVariable id: Long, @RequestBody projectUpdate: ProjectUpdateRequest): ResponseEntity<Project> {
        val updatedProject = service.updatePartial(id, projectUpdate)
        logger.info("Projeto atualizado com sucesso: $id")
        return ResponseEntity.ok(updatedProject)
    }

    @PutMapping("/disable/{id}")
    fun disableProject(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledProject = service.disable(id)
        logger.info("Projeto desabilitado com sucesso: $id")
        return ResponseEntity.ok(disabledProject)
    }

    @PutMapping("/enable/{id}")
    fun enableProject(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledProject = service.enable(id)
        logger.info("Projeto habilitado com sucesso: $id")
        return ResponseEntity.ok(enabledProject)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Any> {
        try {
            service.deleteProject(id)
            logger.info("Projeto deletado com sucesso: $id")
            return ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao deletar projeto $id: ${e.message}")
            return ResponseEntity.notFound().build()
        } catch (e: Exception) {
            logger.error("Erro interno ao deletar projeto $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }

    /**
     * Sincroniza as skills do projeto com as skills dos colaboradores alocados nas squads
     * Busca todas as skills dos colaboradores e atualiza a tabela project_skills
     */
    @PostMapping("/{id}/sync-skills")
    fun syncProjectSkills(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") replaceExisting: Boolean,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        try {
            logger.info("Sincronizando skills do projeto $id (replaceExisting: $replaceExisting)")
            val updatedProject = service.syncProjectSkillsFromSquads(id, authHeader, replaceExisting)
            logger.info("Skills do projeto $id sincronizadas com sucesso. Total de skills: ${updatedProject.requiredSkills.size}")
            return ResponseEntity.ok(mapOf(
                "message" to "Skills sincronizadas com sucesso",
                "projectId" to id,
                "skillsCount" to updatedProject.requiredSkills.size,
                "skills" to updatedProject.requiredSkills.map { 
                    mapOf("id" to it.id, "name" to it.name) 
                }
            ))
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao sincronizar skills do projeto $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao sincronizar skills do projeto $id: ${e.message}", e)
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor: ${e.message}"))
        }
    }

    /**
     * Lista todos os membros dos teams do projeto com suas skills
     * Útil para diagnóstico antes de sincronizar as skills
     */
    @GetMapping("/{id}/team-members")
    fun listProjectTeamMembers(
        @PathVariable id: Long,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        try {
            logger.info("Listando membros dos teams do projeto $id")
            val membersInfo = service.listProjectTeamMembers(id, authHeader)
            logger.info("Membros listados com sucesso para o projeto $id")
            return ResponseEntity.ok(membersInfo)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao listar membros do projeto $id: ${e.message}", e)
            return ResponseEntity.badRequest().body(mapOf(
                "error" to (e.message ?: "Erro desconhecido"),
                "type" to "IllegalArgumentException"
            ))
        } catch (e: Exception) {
            logger.error("Erro interno ao listar membros do projeto $id", e)
            e.printStackTrace()
            return ResponseEntity.internalServerError().body(mapOf(
                "error" to (e.message ?: "Erro desconhecido"),
                "type" to e.javaClass.simpleName,
                "stackTrace" to e.stackTrace.take(5).joinToString("\n") { it.toString() }
            ))
        }
    }

    /**
     * Retorna informações detalhadas dos teams do projeto
     * Inclui: nome do team, membros, skills, valor por hora e valor investido
     */
    @GetMapping("/{id}/team-details")
    fun getProjectTeamDetails(
        @PathVariable id: Long,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Any> {
        try {
            logger.info("Buscando detalhes dos teams do projeto $id")
            val teamDetails = service.getProjectTeamDetails(id, authHeader)
            return ResponseEntity.ok(teamDetails)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao buscar detalhes dos teams do projeto $id: ${e.message}", e)
            return ResponseEntity.badRequest().body(mapOf(
                "error" to (e.message ?: "Erro desconhecido"),
                "type" to "IllegalArgumentException"
            ))
        } catch (e: Exception) {
            logger.error("Erro interno ao buscar detalhes dos teams do projeto $id", e)
            e.printStackTrace()
            return ResponseEntity.internalServerError().body(mapOf(
                "error" to (e.message ?: "Erro desconhecido"),
                "type" to e.javaClass.simpleName
            ))
        }
    }
}


