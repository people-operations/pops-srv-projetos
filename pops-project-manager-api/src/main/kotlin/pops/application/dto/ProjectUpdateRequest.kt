package pops.application.dto

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectUpdateRequest(
    val name: String? = null,
    val typeId: Long? = null,
    val description: String? = null,
    val statusId: Long? = null,
    val budget: BigDecimal? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val area: String? = null,
    val skillIds: List<Long>? = null
)


