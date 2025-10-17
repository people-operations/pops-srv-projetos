package pops.infraestructure.utilities

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

class CrudService<T : Any, ID : Any>(private val repository: JpaRepository<T, ID>) {
    
    fun findAll(): List<T> = repository.findAll()
    
    fun findAllPageable(pageable: Pageable): Page<T> = repository.findAll(pageable)
    
    fun findById(id: ID): T = repository.findById(id)
        .orElseThrow { IllegalArgumentException("Recurso não encontrado com ID: $id") }
    
    @Transactional
    fun save(entity: T): T = repository.save(entity)
    
    @Transactional
    fun update(id: ID, entity: T): T {
        if (!repository.existsById(id)) {
            throw IllegalArgumentException("Recurso não encontrado com ID: $id")
        }
        return repository.save(entity)
    }
    
    @Transactional
    fun delete(id: ID) {
        if (!repository.existsById(id)) {
            throw IllegalArgumentException("Recurso não encontrado com ID: $id")
        }
        repository.deleteById(id)
    }
    
    fun existsById(id: ID): Boolean = repository.existsById(id)
}

