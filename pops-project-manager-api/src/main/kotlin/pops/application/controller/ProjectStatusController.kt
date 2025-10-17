package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.ProjectStatusEntity
import pops.domain.service.ProjectStatusService
import pops.application.dto.ProjectStatusCreateRequest
import pops.application.dto.ProjectStatusUpdateRequest
import pops.application.dto.ProjectStatusResponse

@RestController
@RequestMapping("/project-status")
class ProjectStatusController(
    private val service: ProjectStatusService
) {

    private val logger = LoggerFactory.getLogger(ProjectStatusController::class.java)

    @GetMapping
    fun listActivestatus(): ResponseEntity<Any> {
        val status = service.findActivestatus()
        return if (status.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(status)
    }

    @GetMapping("/inactive")
    fun listInactivestatus(): ResponseEntity<Any> {
        val status = service.findInactivestatus()
        return if (status.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(status)
    }

    @GetMapping("/{id}")
    fun getStatusById(@PathVariable id: Long): ResponseEntity<ProjectStatusResponse> {
        logger.info("Buscando status de projeto com ID: $id")
        try {
            val status = service.findById(id)
            logger.info("Status encontrado: ${status.name}")
            
            val statusResponse = ProjectStatusResponse(
                id = status.id,
                name = status.name,
                description = status.description,
                active = status.active
            )
            
            return ResponseEntity.ok(statusResponse)
        } catch (e: Exception) {
            logger.error("Erro ao buscar status $id: ${e.message}", e)
            throw e
        }
    }

    @PostMapping
    fun createStatus(@RequestBody statusRequest: ProjectStatusCreateRequest): ResponseEntity<ProjectStatusEntity> {
        val status = ProjectStatusEntity(
            name = statusRequest.name,
            description = statusRequest.description
        )
        val newStatus = service.save(status)
        logger.info("Status de projeto criado com sucesso: ${newStatus.id}")
        return ResponseEntity.status(201).body(newStatus)
    }

    @PatchMapping("/{id}")
    fun updateStatus(@PathVariable id: Long, @RequestBody statusUpdate: ProjectStatusUpdateRequest): ResponseEntity<Any> {
        try {
            val updatedStatus = service.updatePartial(id, statusUpdate)
            logger.info("Status de projeto atualizado com sucesso: $id")
            return ResponseEntity.ok(updatedStatus)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao atualizar status $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao atualizar status $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }

    @PutMapping("/disable/{id}")
    fun disableStatus(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledStatus = service.disable(id)
        logger.info("Status de projeto desabilitado com sucesso: $id")
        return ResponseEntity.ok(disabledStatus)
    }

    @PutMapping("/enable/{id}")
    fun enableStatus(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledStatus = service.enable(id)
        logger.info("Status de projeto habilitado com sucesso: $id")
        return ResponseEntity.ok(enabledStatus)
    }

    @DeleteMapping("/{id}")
    fun deleteStatus(@PathVariable id: Long): ResponseEntity<Any> {
        try {
            service.deleteStatus(id)
            logger.info("Status de projeto deletado com sucesso: $id")
            return ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao deletar status $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao deletar status $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }
}
