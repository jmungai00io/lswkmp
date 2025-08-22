package com.lswmobile.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Embedded video model
 */
@Serializable
data class EmbeddedVideo(
    val url: String,
    val source: String,
    @SerialName("_id")
    val id: String? = null
)

/**
 * News item model
 */
@Serializable
data class NewsFeedItem(
    @SerialName("_id")
    val id: String,
    val content: String,
    val images: List<String>? = null,
    val embeddedVideos: List<EmbeddedVideo>? = null,
    val likes: Int,
    val isLiked: Boolean,
    val createdAt: String,
    val author: String? = null,
    val isArchived: Boolean? = false
)

/**
 * News Feed response
 */
@Serializable
data class NewsFeedResponse(
    val data: List<NewsFeedItem>,
    val totalPages: Int
)
