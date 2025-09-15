package com.lswmobile.app.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val success: Boolean,
    val message: String
)
