package pops.application.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectCreateRequest(
    val name: String,
    val typeId: Long?,
    val description: String?,
    val statusId: Long,
    val budget: BigDecimal?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val area: String?,
    val skillIds: List<Long> = emptyList()
)


