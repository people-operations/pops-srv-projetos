package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pops.domain.model.entity.Skill

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByName(name: String): Skill?
    fun existsByName(name: String): Boolean
    fun findByActiveTrue(): List<Skill>
    
    fun findByActiveFalse(): List<Skill>
    
    @Query("SELECT s FROM skill s WHERE s.active = true AND s.type.id = :typeId")
    fun findByActiveTrueAndSkillTypeId(typeId: Long): List<Skill>
    
    fun findByType_Id(typeId: Long): List<Skill>
}


