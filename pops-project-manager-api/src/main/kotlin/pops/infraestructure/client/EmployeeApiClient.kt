package pops.infraestructure.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class EmployeeApiClient(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(EmployeeApiClient::class.java)

    @Value("\${pops-srv-employee.url:http://localhost:8081/api/employees}")
    private lateinit var employeeApiUrl: String

    fun findEmployeeById(employeeId: Long, authToken: String? = null): EmployeeResponse? {
        // Converter Long para Int, pois a API de employee usa Int
        // Verificar se o valor cabe em um Int (Int.MAX_VALUE = 2147483647)
        val employeeIdInt = if (employeeId > Int.MAX_VALUE || employeeId < Int.MIN_VALUE) {
            logger.error("❌ EmployeeId Long ($employeeId) está fora do range de Int (${Int.MIN_VALUE} a ${Int.MAX_VALUE})")
            return null
        } else {
            employeeId.toInt()
        }
        
        return try {
            val headers = HttpHeaders()
            if (authToken != null) {
                headers.set("Authorization", authToken)
            } else {
                logger.warn("Token de autenticação não fornecido para buscar employee $employeeIdInt")
            }
            
            val entity = HttpEntity<String>(headers)
            val url = "$employeeApiUrl/$employeeIdInt"
            
            val response: ResponseEntity<EmployeeResponse> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                EmployeeResponse::class.java
            )
            
            if (response.statusCode.is2xxSuccessful) {
                response.body
            } else {
                logger.warn("Resposta não bem-sucedida ao buscar colaborador $employeeIdInt: ${response.statusCode}")
                null
            }
        } catch (e: org.springframework.web.client.HttpClientErrorException) {
            // 404 é esperado em alguns casos, não precisa logar como erro
            if (e.statusCode.value() != 404) {
                logger.warn("Erro HTTP ao buscar colaborador $employeeIdInt: ${e.statusCode.value()}")
            }
            null
        } catch (e: Exception) {
            logger.error("Erro ao buscar colaborador $employeeIdInt: ${e.message}", e)
            null
        }
    }

    // DTO interno para deserialização da resposta da employee-api
    data class EmployeeResponse(
        val id: Int,
        val name: String,
        val skills: List<EmployeeSkillResponse> = emptyList(),
        val contractWage: Double? = null,
        val workHoursPerWeek: Int? = null,
        val jobTitle: String? = null
    )

    data class EmployeeSkillResponse(
        val id: Int,
        val name: String,
        val skillType: EmployeeSkillTypeResponse? = null
    )

    data class EmployeeSkillTypeResponse(
        val id: Int? = null,
        val name: String? = null
    )
}

