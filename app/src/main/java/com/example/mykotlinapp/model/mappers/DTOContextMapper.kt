package com.example.mykotlinapp.model.mappers

import android.content.Context

interface DTOContextMapper<Entity, DTO> {

    /**
     * Maps a database entity to a DTO object to display on a UI with a provided context
     *
     * @param context The application context used to retrieve resource values
     * @return The function that maps an entity to a DTO
     */
    fun toDTO(context: Context): (entity: Entity) -> DTO
}