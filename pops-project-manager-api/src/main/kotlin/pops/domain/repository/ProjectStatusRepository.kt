package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pops.domain.model.entity.ProjectStatusEntity

@Repository
interface ProjectStatusRepository : JpaRepository<ProjectStatusEntity, Long> {

    fun findByActiveTrue(): List<ProjectStatusEntity>

    fun findByActiveFalse(): List<ProjectStatusEntity>

    fun existsByName(name: String): Boolean
}
