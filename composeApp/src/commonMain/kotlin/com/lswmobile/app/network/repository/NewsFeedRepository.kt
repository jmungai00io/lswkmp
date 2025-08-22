package com.lswmobile.app.network.repository

import com.lswmobile.app.network.LivestockWealthApi
import com.lswmobile.app.network.model.NewsFeedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsFeedRepository(
    private val api: LivestockWealthApi
) {
    
    /**
     * Fetch news feed with pagination
     */
    fun getNewsFeed(page: Int = 1, limit: Int = 10): Flow<Result<NewsFeedResponse>> = flow {
        try {
            val response = api.getNewsFeed(page = page, limit = limit)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    /**
     * Like or unlike a news item
     */
    fun toggleLike(newsId: String): Flow<Result<Unit>> = flow {
        try {
            api.toggleNewsLike(newsId)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
} 