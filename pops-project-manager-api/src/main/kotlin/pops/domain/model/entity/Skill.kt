package pops.domain.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.FetchType
import pops.domain.model.entity.SkillTypeEntity

@Entity(name = "skill")
data class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 100)
    val name: String,
    
    @Column(length = 500)
    val description: String?,
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_type_id", nullable = false)
    val type: SkillTypeEntity,
    
    @Column(nullable = false)
    val active: Boolean = true
)


