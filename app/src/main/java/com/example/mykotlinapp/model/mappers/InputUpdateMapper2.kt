package com.example.mykotlinapp.model.mappers

import com.example.mykotlinapp.model.dto.inputs.form.InputDTO
import com.example.mykotlinapp.utils.TimeProvider

interface InputUpdateMapper2<Input : InputDTO, Entity> {

    /**
     * Maps an update form input to a database entity
     *
     * @param inputData The update form input
     * @return The resulting database entity
     */
    fun toLocalUpdate(inputData: Input, timeProvider: TimeProvider): ((Entity) -> Entity)
}