package pops.domain.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "skill_type")
data class SkillTypeEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 50, unique = true)
    val name: String,
    
    @Column(length = 200)
    val description: String?,
    
    @Column(nullable = false)
    val active: Boolean = true
)

