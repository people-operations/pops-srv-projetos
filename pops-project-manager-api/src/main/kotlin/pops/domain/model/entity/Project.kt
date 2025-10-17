package pops.domain.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.JoinTable
import jakarta.persistence.JoinColumn
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import pops.domain.model.entity.ProjectStatusEntity
import pops.domain.model.entity.ProjectTypeEntity
import java.math.BigDecimal
import java.time.LocalDate

@Entity(name = "project")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 200)
    val name: String,
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_type_id")
    val type: ProjectTypeEntity?,
    
    @Column(length = 1000)
    val description: String?,
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_status_id", nullable = false)
    val status: ProjectStatusEntity,
    
    @Column(precision = 15, scale = 2)
    val budget: BigDecimal?,
    
    @Column(name = "start_date")
    val startDate: LocalDate?,
    
    @Column(name = "end_date")
    val endDate: LocalDate?,
    
    @Column(length = 100)
    val area: String?,
    
    @Column(nullable = false)
    val active: Boolean = true,
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "project_skills",
        joinColumns = [JoinColumn(name = "project_id")],
        inverseJoinColumns = [JoinColumn(name = "skill_id")]
    )
    val requiredSkills: MutableSet<Skill> = mutableSetOf()
)


