package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.SkillTypeEntity
import pops.domain.service.SkillTypeService
import pops.application.dto.SkillTypeCreateRequest
import pops.application.dto.SkillTypeUpdateRequest
import pops.application.dto.SkillTypeResponse

@RestController
@RequestMapping(value = ["/api/skill-types", "/skill-types"])
class SkillTypeController(
    private val service: SkillTypeService
) {

    private val logger = LoggerFactory.getLogger(SkillTypeController::class.java)

    @GetMapping
    fun listActiveTypes(): ResponseEntity<Any> {
        val types = service.findActiveTypes()
        return if (types.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(types)
    }

    @GetMapping("/inactive")
    fun listInactiveTypes(): ResponseEntity<Any> {
        val types = service.findInactiveTypes()
        return if (types.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(types)
    }

    @GetMapping("/{id}")
    fun getTypeById(@PathVariable id: Long): ResponseEntity<SkillTypeResponse> {
        logger.info("Buscando tipo de skill com ID: $id")
        try {
            val type = service.findById(id)
            logger.info("Tipo encontrado: ${type.name}")
            
            val typeResponse = SkillTypeResponse(
                id = type.id,
                name = type.name,
                description = type.description,
                active = type.active
            )
            
            return ResponseEntity.ok(typeResponse)
        } catch (e: Exception) {
            logger.error("Erro ao buscar tipo $id: ${e.message}", e)
            throw e
        }
    }

    @PostMapping
    fun createType(@RequestBody typeRequest: SkillTypeCreateRequest): ResponseEntity<SkillTypeEntity> {
        val type = SkillTypeEntity(
            name = typeRequest.name,
            description = typeRequest.description
        )
        val newType = service.save(type)
        logger.info("Tipo de skill criado com sucesso: ${newType.id}")
        return ResponseEntity.status(201).body(newType)
    }

    @PatchMapping("/{id}")
    fun updateType(@PathVariable id: Long, @RequestBody typeUpdate: SkillTypeUpdateRequest): ResponseEntity<Any> {
        try {
            val updatedType = service.updatePartial(id, typeUpdate)
            logger.info("Tipo de skill atualizado com sucesso: $id")
            return ResponseEntity.ok(updatedType)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao atualizar tipo $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao atualizar tipo $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }

    @PutMapping("/disable/{id}")
    fun disableType(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledType = service.disable(id)
        logger.info("Tipo de skill desabilitado com sucesso: $id")
        return ResponseEntity.ok(disabledType)
    }

    @PutMapping("/enable/{id}")
    fun enableType(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledType = service.enable(id)
        logger.info("Tipo de skill habilitado com sucesso: $id")
        return ResponseEntity.ok(enabledType)
    }

    @DeleteMapping("/{id}")
    fun deleteType(@PathVariable id: Long): ResponseEntity<Any> {
        try {
            service.deleteType(id)
            logger.info("Tipo de skill deletado com sucesso: $id")
            return ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao deletar tipo $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao deletar tipo $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }
}
