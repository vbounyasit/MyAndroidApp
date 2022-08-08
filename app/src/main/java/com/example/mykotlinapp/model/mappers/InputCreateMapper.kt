package com.example.mykotlinapp.model.mappers

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

interface InputCreateMapper<Input : InputDTO, Entity> {

    /**
     * Maps a creation form input to a database entity
     *
     * @param input The creation form input
     * @return The resulting database entity
     */
    fun toEntity(input: Input): Entity
}