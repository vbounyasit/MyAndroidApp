package com.example.mykotlinapp.model.mappers

interface DTOMapper<Entity, DTO> {

    /**
     * Maps a database entity to a DTO object to display on a UI
     *
     * @param entity The input domain model
     * @return The resulting DTO object
     */
    fun toDTO(entity: Entity): DTO
}