package com.example.mykotlinapp.model.mappers

interface DTOMapperWithParam<Entity, DTO, Parameter> {

    /**
     * Maps a database entity to a DTO object to display on a UI with a provided context
     *
     * @param parameter The application context used to retrieve resource values
     * @return The function that maps an entity to a DTO
     */
    fun toDTO(parameter: Parameter): (entity: Entity) -> DTO
}