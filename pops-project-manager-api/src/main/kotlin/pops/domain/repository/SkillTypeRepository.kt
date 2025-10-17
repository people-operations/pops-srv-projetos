package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pops.domain.model.entity.SkillTypeEntity

@Repository
interface SkillTypeRepository : JpaRepository<SkillTypeEntity, Long> {
    fun findByActiveTrue(): List<SkillTypeEntity>
    fun findByActiveFalse(): List<SkillTypeEntity>
    fun existsByName(name: String): Boolean
}




