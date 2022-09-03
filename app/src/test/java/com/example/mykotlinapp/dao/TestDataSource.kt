package com.example.mykotlinapp.dao

class TestDataSource<Index, Entity>(val getIndex: (Entity) -> Index) {

    private val localStorage: MutableMap<Index, Entity> = mutableMapOf()

    fun insert(entity: Entity) {
        if (!localStorage.containsKey(getIndex(entity)))
            localStorage[getIndex(entity)] = entity
    }

    fun insert(entities: List<Entity>) {
        entities
            .filter { !localStorage.containsKey(getIndex(it)) }
            .map { Pair(getIndex(it), it) }
            .let(localStorage::putAll)
    }

    fun get(index: Index): Entity? = localStorage[index]

    fun getAll(): List<Entity> = localStorage.values.toList()

    fun getWhen(predicate: (Entity) -> Boolean): Entity? = localStorage.values.find(predicate)

    fun getAllWhen(predicate: (Entity) -> Boolean): List<Entity> = localStorage.values.filter(predicate)

    fun update(entity: Entity) {
        localStorage[getIndex(entity)] = entity
    }

    fun updateAll(entities: List<Entity>) = localStorage.putAll(entities.map { Pair(getIndex(it), it) })

    fun update(index: Index, transformation: (Entity) -> Entity) {
        localStorage[index]?.let { localStorage[index] = transformation(it) }
    }

    fun update(indexes: List<Index>, transformation: (Entity) -> Entity) = indexes.forEach { update(it, transformation) }

    fun delete(index: Index) {
        localStorage.remove(index)
    }

    fun deleteWhen(predicate: (Entity) -> Boolean) =
        localStorage.filterValues(predicate).keys.forEach(localStorage::remove)

    fun deleteAll(entities: List<Entity>) = entities.map(getIndex).forEach(localStorage::remove)

    fun <OtherEntity> leftJoin(
        dataSource: TestDataSource<*, OtherEntity>,
        joinPredicate: (Entity, OtherEntity) -> Boolean,
    ): Map<Entity, List<OtherEntity>> =
        localStorage.values.associateWith { entity ->
            dataSource.localStorage.values.filter { otherEntity -> joinPredicate(entity, otherEntity) }
        }

    fun <OtherEntity> join(
        dataSource: TestDataSource<*, OtherEntity>,
        joinPredicate: (Entity, OtherEntity) -> Boolean
    ): Map<Entity, OtherEntity> =
        leftJoin(dataSource, joinPredicate).filterValues { it.isNotEmpty() }.mapValues { it.value.first() }


    fun clear() = localStorage.clear()

}