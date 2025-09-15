package com.lswmobile.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lswmobile.app.network.model.NewsFeedItem
import com.lswmobile.app.network.model.NewsFeedResponse
import com.lswmobile.app.network.repository.NewsFeedRepository
import com.lswmobile.app.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val repository: NewsFeedRepository,
    private val coroutineScope: CoroutineScope
) {
    
    // State
    private val _newsItems = MutableStateFlow<List<NewsFeedItem>>(emptyList())
    val newsItems: StateFlow<List<NewsFeedItem>> = _newsItems.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()
    
    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()
    
    // Pagination state
    var isRefreshing by mutableStateOf(false)
        private set
    
    var isLoadingMore by mutableStateOf(false)
        private set
    
    init {
        loadNewsFeed()
    }
    
    /**
     * Load news feed (first page)
     */
    fun loadNewsFeed() {
        coroutineScope.launch(Dispatchers.Main) {
            _isLoading.value = true
            _error.value = null
            
            repository.getNewsFeed(page = 1, limit = 10).collect { result ->
                _isLoading.value = false
                
                result.fold(
                    onSuccess = { response ->
                        _newsItems.value = response.data
                        _currentPage.value = 1
                        _totalPages.value = response.totalPages
                        _hasMorePages.value = response.totalPages > 1
                    },
                    onFailure = { exception ->
                        val errorException = if (exception is Exception) exception else Exception(exception.message, exception)
                        _error.value = ErrorUtils.extractErrorMessage(errorException, "Failed to load news feed")
                    }
                )
            }
        }
    }
    
    /**
     * Load more news items (pagination)
     */
    fun loadMoreNews() {
        if (isLoadingMore || !_hasMorePages.value) return
        
        coroutineScope.launch(Dispatchers.Main) {
            isLoadingMore = true
            
            val nextPage = _currentPage.value + 1
            repository.getNewsFeed(page = nextPage, limit = 10).collect { result ->
                isLoadingMore = false
                
                result.fold(
                    onSuccess = { response ->
                        val currentItems = _newsItems.value.toMutableList()
                        currentItems.addAll(response.data)
                        _newsItems.value = currentItems
                        _currentPage.value = nextPage
                        _hasMorePages.value = nextPage < response.totalPages
                    },
                    onFailure = { exception ->
                        val errorException = if (exception is Exception) exception else Exception(exception.message, exception)
                        _error.value = ErrorUtils.extractErrorMessage(errorException, "Failed to load more news")
                    }
                )
            }
        }
    }
    
    /**
     * Refresh news feed
     */
    fun refreshNewsFeed() {
        coroutineScope.launch(Dispatchers.Main) {
            isRefreshing = true
            
            repository.getNewsFeed(page = 1, limit = 10).collect { result ->
                isRefreshing = false
                
                result.fold(
                    onSuccess = { response ->
                        _newsItems.value = response.data
                        _currentPage.value = 1
                        _totalPages.value = response.totalPages
                        _hasMorePages.value = response.totalPages > 1
                        _error.value = null
                    },
                    onFailure = { exception ->
                        val errorException = if (exception is Exception) exception else Exception(exception.message, exception)
                        _error.value = ErrorUtils.extractErrorMessage(errorException, "Failed to refresh news feed")
                    }
                )
            }
        }
    }
    
    /**
     * Toggle like on a news item
     */
    fun toggleLike(newsId: String) {
        coroutineScope.launch(Dispatchers.Main) {
            repository.toggleLike(newsId).collect { result ->
                result.fold(
                    onSuccess = {
                        // Update the like status in the local list
                        val updatedItems = _newsItems.value.map { item ->
                            if (item.id == newsId) {
                                item.copy(
                                    isLiked = !item.isLiked,
                                    likes = if (item.isLiked) item.likes - 1 else item.likes + 1
                                )
                            } else {
                                item
                            }
                        }
                        _newsItems.value = updatedItems
                    },
                    onFailure = { exception ->
                        val errorException = if (exception is Exception) exception else Exception(exception.message, exception)
                        _error.value = ErrorUtils.extractErrorMessage(errorException, "Failed to update like")
                    }
                )
            }
        }
    }
    
    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }
} 