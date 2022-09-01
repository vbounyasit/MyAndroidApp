package com.example.mykotlinapp

class TestLocalStorage<Entity, Index>(val getIndex: (Entity) -> Index) {

    private val localStorage: MutableList<Entity> = mutableListOf()

    fun insert(entity: Entity): Boolean = localStorage.add(entity)

    fun insert(entities: List<Entity>): Boolean = localStorage.addAll(entities)

    fun get(index: Index): Entity? = localStorage.find { getIndex(it) == index }

    fun getWhen(predicate: (Entity) -> Boolean): Entity? = localStorage.find(predicate)

    fun getAllWhen(predicate: (Entity) -> Boolean): List<Entity> = localStorage.filter(predicate)

    fun update(entity: Entity, upsert: Boolean = true): Boolean {
        val existingIndex = localStorage.indexOfFirst { getIndex(it) == getIndex(entity) }
        return if (existingIndex >= 0) localStorage.set(existingIndex, entity) != entity
        else if (upsert) localStorage.add(entity)
        else false
    }

    fun delete(index: Index): Boolean = localStorage.removeIf { getIndex(it) == index }

    fun deleteWhen(predicate: (Entity) -> Boolean) = localStorage.removeIf(predicate)

    fun deleteEntities(entities: List<Entity>): Boolean {
        val indexes = entities.map(getIndex)
        return localStorage.removeIf { indexes.contains(getIndex(it)) }
    }

    fun clear() {
        localStorage.clear()
    }

}