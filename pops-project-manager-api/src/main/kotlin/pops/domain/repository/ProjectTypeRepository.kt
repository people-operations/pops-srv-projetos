package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pops.domain.model.entity.ProjectTypeEntity

@Repository
interface ProjectTypeRepository : JpaRepository<ProjectTypeEntity, Long> {
    fun findByActiveTrue(): List<ProjectTypeEntity>
    fun findByActiveFalse(): List<ProjectTypeEntity>
    fun existsByName(name: String): Boolean
}




