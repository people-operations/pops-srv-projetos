package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Skill
import pops.domain.service.SkillService
import pops.application.dto.SkillCreateRequest
import pops.application.dto.SkillUpdateRequest
import pops.application.dto.SkillResponse
import pops.application.dto.SkillTypeResponse

@RestController
@RequestMapping(value = ["/api/skills", "/skills"])
class SkillController(
    private val service: SkillService
) {

    private val logger = LoggerFactory.getLogger(SkillController::class.java)

    @GetMapping
    fun listSkills(): ResponseEntity<Any> {
        val skills = service.findActiveSkills()
        return if (skills.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills)
    }

    @GetMapping("/inactive")
    fun listInactiveSkills(): ResponseEntity<Any> {
        val skills = service.findInactiveSkills()
        return if (skills.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills)
    }

    @GetMapping("/pageable")
    fun listSkillsPagination(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Any> {
        val skills = service.findAllPageable(pageable)
        return if (skills.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills.content)
    }

    @GetMapping("/type/{typeId}")
    fun listSkillsByType(@PathVariable typeId: Long): ResponseEntity<Any> {
        val skills = service.findActiveSkillsByType(typeId)
        return if (skills.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills)
    }

    @GetMapping("/{id}")
    fun getSkillById(@PathVariable id: Long): ResponseEntity<SkillResponse> {
        logger.info("Buscando skill com ID: $id")
        try {
            val skill = service.findById(id)
            logger.info("Skill encontrada: ${skill.name}")
            
            val typeResponse = SkillTypeResponse(
                id = skill.type.id,
                name = skill.type.name,
                description = skill.type.description,
                active = skill.type.active
            )
            
            val skillResponse = SkillResponse(
                id = skill.id,
                name = skill.name,
                description = skill.description,
                type = typeResponse,
                active = skill.active
            )
            
            return ResponseEntity.ok(skillResponse)
        } catch (e: Exception) {
            logger.error("Erro ao buscar skill $id: ${e.message}", e)
            throw e
        }
    }

    @PostMapping
    fun createSkill(@RequestBody skillRequest: SkillCreateRequest): ResponseEntity<Skill> {
        val newSkill = service.saveWithType(
            name = skillRequest.name,
            description = skillRequest.description,
            typeId = skillRequest.typeId
        )
        logger.info("Skill criada com sucesso: ${newSkill.id}")
        return ResponseEntity.status(201).body(newSkill)
    }

    @PatchMapping("/{id}")
    fun updateSkill(@PathVariable id: Long, @RequestBody skillUpdate: SkillUpdateRequest): ResponseEntity<Any> {
        try {
            val updatedSkill = service.updatePartial(id, skillUpdate)
            logger.info("Skill atualizada com sucesso: $id")
            return ResponseEntity.ok(updatedSkill)
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao atualizar skill $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao atualizar skill $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }

    @PutMapping("/disable/{id}")
    fun disableSkill(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledSkill = service.disable(id)
        logger.info("Skill desabilitada com sucesso: $id")
        return ResponseEntity.ok(disabledSkill)
    }

    @PutMapping("/enable/{id}")
    fun enableSkill(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledSkill = service.enable(id)
        logger.info("Skill habilitada com sucesso: $id")
        return ResponseEntity.ok(enabledSkill)
    }

    @DeleteMapping("/{id}")
    fun deleteSkill(@PathVariable id: Long): ResponseEntity<Any> {
        try {
            service.deleteSkill(id)
            logger.info("Skill deletada com sucesso: $id")
            return ResponseEntity.noContent().build()
        } catch (e: IllegalArgumentException) {
            logger.error("Erro ao deletar skill $id: ${e.message}")
            return ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("Erro interno ao deletar skill $id: ${e.message}")
            return ResponseEntity.internalServerError().body(mapOf("error" to "Erro interno do servidor"))
        }
    }
}


