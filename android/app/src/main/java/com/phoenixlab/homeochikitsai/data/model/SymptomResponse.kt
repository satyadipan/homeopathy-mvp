package com.phoenixlab.homeochikitsai.data.model

data class SymptomResponse(
    val case_id: String,
    val suggested: List<Suggestion>
)
