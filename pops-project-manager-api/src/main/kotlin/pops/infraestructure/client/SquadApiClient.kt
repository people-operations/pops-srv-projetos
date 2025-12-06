package pops.infraestructure.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import pops.application.dto.SquadDTO
import pops.application.dto.SquadMemberDTO
import pops.application.dto.MemberSkillDTO
import pops.application.dto.SkillResponse
import pops.application.dto.SkillTypeResponse
import pops.domain.repository.SkillRepository

@Component
class SquadApiClient(
    private val restTemplate: RestTemplate,
    private val employeeApiClient: EmployeeApiClient,
    private val skillRepository: SkillRepository
) {
    private val logger = LoggerFactory.getLogger(SquadApiClient::class.java)

    @Value("\${pops-srv-squad.url:http://localhost:8083/api-squad}")
    private lateinit var squadApiUrl: String

    fun findTeamsByProjectId(projectId: Long, authToken: String? = null): List<SquadDTO> {
        return try {
            val headers = HttpHeaders()
            if (authToken != null) {
                headers.set("Authorization", authToken)
            }
            
            val entity = HttpEntity<String>(headers)
            val url = "$squadApiUrl/teams/project/$projectId"
            
            val response: ResponseEntity<Array<TeamResponse>> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Array<TeamResponse>::class.java
            )
            
            // Verificar se a resposta foi bem-sucedida
            if (!response.statusCode.is2xxSuccessful) {
                logger.warn("Resposta não bem-sucedida ao buscar teams do projeto $projectId: ${response.statusCode}")
                return emptyList()
            }
            
            val teams = response.body?.toList() ?: emptyList()
            
            if (teams.isEmpty()) {
                return emptyList()
            }
            
            // Para cada team, buscar os allocations (integrantes) e skills
            teams.map { team ->
                val members = findTeamMembers(team.id, authToken)
                val skills = aggregateTeamSkills(members, authToken)
                SquadDTO(
                    id = team.id,
                    name = team.name,
                    description = team.description,
                    members = members,
                    skills = skills,
                    po = team.approver?.name
                )
            }
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            if (e.statusCode.value() != 404 && e.statusCode.value() != 204) {
                logger.error("Erro HTTP ao buscar teams do projeto $projectId: ${e.statusCode.value()}", e)
            }
            emptyList()
        } catch (e: Exception) {
            logger.error("Erro ao buscar teams do projeto $projectId: ${e.message}", e)
            emptyList()
        }
    }

    private fun findTeamMembers(teamId: Long, authToken: String?): List<SquadMemberDTO> {
        return try {
            val headers = HttpHeaders()
            authToken?.let { headers.set("Authorization", it) }
            
            val entity = HttpEntity<String>(headers)
            val url = "$squadApiUrl/teams/$teamId/allocations"
            
            val response: ResponseEntity<Array<AllocationResponse>> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Array<AllocationResponse>::class.java
            )
            
            val allocations = response.body?.toList() ?: emptyList()
            allocations.mapNotNull { allocation ->
                // Sempre usar o personId da allocation (fk_person) como fonte de verdade
                val personId = allocation.personId
                if (personId != null) {
                    // Tentar buscar o employee pelo personId (sempre buscar, mesmo se vier na allocation)
                    // Isso garante que temos o nome correto e as skills atualizadas
                    var employeeName = "N/A"
                    var employee: EmployeeApiClient.EmployeeResponse? = null
                    
                    try {
                        employee = employeeApiClient.findEmployeeById(personId, authToken)
                        if (employee != null && employee.name.isNotBlank()) {
                            employeeName = employee.name
                        } else {
                            // Se não encontrou, tentar usar o nome que veio na allocation (se houver)
                            if (allocation.employee != null && allocation.employee.name.isNotBlank()) {
                                employeeName = allocation.employee.name
                            }
                        }
                    } catch (e: Exception) {
                        // Fallback: usar nome da allocation se disponível
                        if (allocation.employee != null && allocation.employee.name.isNotBlank()) {
                            employeeName = allocation.employee.name
                        } else {
                            logger.warn("Erro ao buscar employee $personId: ${e.message}")
                        }
                    }
                    
                    // Usar o ID do employee se encontrado, senão usar o personId
                    val memberId = employee?.id?.toLong() ?: personId
                    
                    // Mapear skills do employee para MemberSkillDTO
                    val memberSkills = employee?.skills?.map { skill ->
                        MemberSkillDTO(
                            id = skill.id,
                            name = skill.name,
                            skillType = skill.skillType?.name
                        )
                    } ?: emptyList()
                    
                    SquadMemberDTO(
                        id = memberId,
                        name = employeeName,
                        position = allocation.position,
                        allocatedHours = allocation.allocatedHours,
                        skills = memberSkills
                    )
                } else {
                    null
                }
            }
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            // Se for 404, significa que não há allocations para esse team (é normal)
            if (e.statusCode.value() != 404) {
                logger.warn("Erro HTTP ao buscar integrantes do team $teamId: ${e.statusCode.value()}")
            }
            emptyList()
        } catch (e: Exception) {
            logger.warn("Erro ao buscar integrantes do team $teamId: ${e.message}")
            emptyList()
        }
    }

    // DTOs internos para deserialização da resposta da squad-api
    private data class TeamResponse(
        val id: Long,
        val name: String,
        val description: String?,
        val sprintDuration: Int,
        val approverId: Long?,
        val projectId: Long,
        val status: Boolean,
        val approver: ApproverDTO?
    )

    private data class ApproverDTO(
        val id: Long,
        val name: String
    )

    private data class AllocationResponse(
        val id: Long,
        val startedAt: String,
        val allocatedHours: Int,
        val team: TeamShortDTO,
        val personId: Long?,
        val employee: EmployeeDTO?,
        val position: String
    )

    private data class TeamShortDTO(
        val id: Long,
        val name: String
    )

    private data class EmployeeDTO(
        val id: Long,
        val name: String
    )

    /**
     * Agrega as skills de todos os membros do team, retornando uma lista única de skills
     */
    private fun aggregateTeamSkills(members: List<SquadMemberDTO>, authToken: String?): List<SkillResponse> {
        if (members.isEmpty()) {
            return emptyList()
        }

        val allSkills = mutableSetOf<String>() // Usar Set para evitar duplicatas por nome
        val skillMap = mutableMapOf<String, EmployeeApiClient.EmployeeSkillResponse>()

        // Buscar skills de cada membro
        members.forEach { member ->
            try {
                val employee = employeeApiClient.findEmployeeById(member.id, authToken)
                employee?.skills?.forEach { skill ->
                    if (!allSkills.contains(skill.name)) {
                        allSkills.add(skill.name)
                        skillMap[skill.name] = skill
                    }
                }
            } catch (e: Exception) {
                // Log apenas em caso de erro crítico
            }
        }

        // Mapear skills do formato Employee para SkillResponse
        // Buscar skills correspondentes no banco do project-manager-api pelo nome
        return allSkills.mapNotNull { skillName ->
            try {
                val skillEntity = skillRepository.findByNameIgnoreCaseAndActiveTrue(skillName)
                if (skillEntity != null) {
                    SkillResponse(
                        id = skillEntity.id,
                        name = skillEntity.name,
                        description = skillEntity.description,
                        type = SkillTypeResponse(
                            id = skillEntity.type.id,
                            name = skillEntity.type.name,
                            description = skillEntity.type.description,
                            active = skillEntity.type.active
                        ),
                        active = skillEntity.active
                    )
                } else {
                    // Se a skill não existe no banco, criar uma SkillResponse básica
                    // (pode ser uma skill que existe no Odoo mas não foi cadastrada aqui)
                    val employeeSkill = skillMap[skillName]
                    SkillResponse(
                        id = null,
                        name = skillName,
                        description = null,
                        type = SkillTypeResponse(
                            id = null,
                            name = employeeSkill?.skillType?.name ?: "HARD",
                            description = null,
                            active = true
                        ),
                        active = true
                    )
                }
            } catch (e: Exception) {
                // Log apenas em caso de erro crítico
                null
            }
        }.distinctBy { it.name } // Garantir que não há duplicatas
    }
    
    /**
     * Busca todas as skills dos colaboradores alocados em todas as squads de um projeto
     * Retorna uma lista de nomes de skills únicos
     */
    fun getAllProjectSkillsNames(projectId: Long, authToken: String? = null): Set<String> {
        val squads = findTeamsByProjectId(projectId, authToken)
        val allSkillNames = mutableSetOf<String>()
        
        squads.forEach { squad ->
            squad.members.forEach { member ->
                try {
                    val employee = employeeApiClient.findEmployeeById(member.id, authToken)
                    employee?.skills?.forEach { skill ->
                        allSkillNames.add(skill.name)
                    }
                } catch (e: Exception) {
                    // Log apenas em caso de erro crítico
                }
            }
        }
        
        return allSkillNames
    }
}

