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
import pops.application.dto.SkillResponse
import pops.application.dto.ProjectTypeResponse
import pops.application.dto.ProjectStatusResponse
import pops.infraestructure.utilities.CrudService

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val service: ProjectService,
) {

    private val logger = LoggerFactory.getLogger(ProjectController::class.java)

    @GetMapping
    fun listActiveProjects(): ResponseEntity<Any> {
        val projects = service.findActiveProjects()
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/inactive")
    fun listInactiveProjects(): ResponseEntity<Any> {
        val projects = service.findInactiveProjects()
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/pageable")
    fun listProjectsPagination(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Any> {
        val projects = service.findAllPageable(pageable)
        return if (projects.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects.content)
    }

    @GetMapping("/status/{statusId}")
    fun listProjectsByStatus(@PathVariable statusId: Long): ResponseEntity<Any> {
        val projects = service.findActiveProjectsByStatus(statusId)
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<Any> {
        logger.info("Buscando projeto com ID: $id")
        try {
            val project = service.findById(id)
            logger.info("Projeto encontrado: ${project.name}")
            
            // Retornar o projeto diretamente primeiro para testar
            return ResponseEntity.ok(project)
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
}


