package com.phoenixlab.homeochikitsai.data.model

data class Suggestion(
    val name: String,
    val potency: String,
    val dosage: String,
    val rationale: String,
    val confidence: Double
)

