package com.example.mykotlinapp.model.mappers

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO

interface InputUpdateMapper<Input : InputDTO, Entity> {

    /**
     * Maps an update form input to a database entity
     *
     * @param inputData The update form input
     * @return The resulting database entity
     */
    fun toLocalUpdateWithInput(inputData: Input): ((Entity) -> Entity)
}