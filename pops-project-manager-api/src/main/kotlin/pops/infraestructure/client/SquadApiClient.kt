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

@Component
class SquadApiClient(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(SquadApiClient::class.java)

    @Value("\${pops-srv-squad.url:http://localhost:8083/api-squad}")
    private lateinit var squadApiUrl: String

    fun findTeamsByProjectId(projectId: Long, authToken: String? = null): List<SquadDTO> {
        return try {
            val headers = HttpHeaders()
            authToken?.let { headers.set("Authorization", it) }
            
            val entity = HttpEntity<String>(headers)
            val url = "$squadApiUrl/teams/project/$projectId"
            
            logger.info("Buscando teams do projeto $projectId na URL: $url")
            
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
            logger.info("Encontrados ${teams.size} teams para o projeto $projectId")
            
            if (teams.isEmpty()) {
                logger.info("Nenhum team encontrado para o projeto $projectId")
                return emptyList()
            }
            
            // Para cada team, buscar os allocations (integrantes)
            teams.map { team ->
                logger.debug("Processando team ${team.id} - ${team.name}")
                val members = findTeamMembers(team.id, authToken)
                SquadDTO(
                    id = team.id,
                    name = team.name,
                    description = team.description,
                    members = members,
                    skills = emptyList(), // Skills não estão disponíveis na squad-api atualmente
                    po = team.approver?.name
                )
            }
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            if (e.statusCode.value() == 404 || e.statusCode.value() == 204) {
                logger.info("Nenhum team encontrado para o projeto $projectId (status: ${e.statusCode.value()})")
            } else {
                logger.error("Erro HTTP ao buscar teams do projeto $projectId: ${e.statusCode.value()} - ${e.message}", e)
            }
            emptyList()
        } catch (e: Exception) {
            logger.error("Erro ao buscar teams do projeto $projectId: ${e.message}", e)
            e.printStackTrace()
            emptyList() // Retorna lista vazia em caso de erro para não quebrar o fluxo
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
                val memberId = allocation.employee?.id ?: allocation.personId
                if (memberId != null) {
                    SquadMemberDTO(
                        id = memberId,
                        name = allocation.employee?.name ?: "N/A",
                        position = allocation.position,
                        allocatedHours = allocation.allocatedHours
                    )
                } else null
            }
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
}

