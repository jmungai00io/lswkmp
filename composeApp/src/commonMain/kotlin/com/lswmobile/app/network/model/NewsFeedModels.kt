package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * News Feed response
 */
@Serializable
data class NewsFeedResponse(
    val news: List<NewsFeedItem>,
    val count: Int,
    val message: String
)

/**
 * News item model
 */
@Serializable
data class NewsFeedItem(
    @SerialName("_id")
    val _id: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val createdAt: String,
    val updatedAt: String
)
