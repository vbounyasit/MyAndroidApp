package com.example.mykotlinapp.model.mappers

interface NetworkRequestMapper<Entity, NetworkRequest> {

    /**
     * Maps a database entity to an API request object
     *
     * @param entity The database entity
     * @return The resulting network request
     */
    fun toNetworkRequest(entity: Entity): NetworkRequest
}