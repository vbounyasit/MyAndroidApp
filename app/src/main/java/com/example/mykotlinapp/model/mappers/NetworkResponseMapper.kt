package com.example.mykotlinapp.model.mappers

interface NetworkResponseMapper<NetworkResponse, Entity> {

    /**
     * Maps an API network response to a database entity
     *
     * @param networkData The API response object
     * @return The resulting database entity
     */
    fun toEntity(networkData: NetworkResponse): Entity
}