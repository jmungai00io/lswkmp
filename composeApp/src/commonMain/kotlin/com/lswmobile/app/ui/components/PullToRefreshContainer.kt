package com.lswmobile.app.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A container that provides pull-to-refresh functionality with iOS-like physics
 * Only triggers refresh when at the top of the scrollable content
 * 
 * @param isRefreshing Whether the content is currently refreshing
 * @param onRefresh Callback to be invoked when a refresh is triggered
 * @param modifier Modifier to be applied to the container
 * @param lazyGridState Optional LazyGridState to check scroll position
 * @param lazyListState Optional LazyListState to check scroll position
 * @param content The content to be displayed inside the container
 */
@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState? = null,
    lazyListState: LazyListState? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val refreshTriggerDistance = with(LocalDensity.current) { 80.dp.toPx() }
    var refreshing by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    
    // Check if we're at the top of the scrollable content
    val isAtTop by remember {
        derivedStateOf {
            when {
                lazyGridState != null -> {
                    lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0
                }
                lazyListState != null -> {
                    lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
                }
                else -> true // If no state provided, assume we can refresh (backward compatibility)
            }
        }
    }
    
    // Update refreshing state based on isRefreshing parameter
    LaunchedEffect(isRefreshing) {
        refreshing = isRefreshing
    }
    
    // If refresh triggered, invoke onRefresh callback
    LaunchedEffect(refreshing) {
        if (refreshing) {
            onRefresh()
        }
    }
    
    // When refresh completes, animate progress back to 0
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing && progress > 0f) {
            animate(initialValue = progress, targetValue = 0f) { value, _ ->
                progress = value
            }
        }
    }
    
    // Create nested scroll connection to track pull gesture
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
                // Only allow pull-to-refresh when at the top of the content
                // If refreshing, scrolling down, or not at top, let the scroll happen normally
                if (refreshing || available.y < 0 || !isAtTop) return androidx.compose.ui.geometry.Offset.Zero
                
                // Calculate new progress value
                val newProgress = (progress + available.y / refreshTriggerDistance).coerceIn(0f, 1.3f)
                progress = newProgress
                
                // Consume the scroll if pulling down
                return if (available.y > 0) available else androidx.compose.ui.geometry.Offset.Zero
            }
            
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // If progress crosses the threshold and we're at the top, trigger refresh
                if (progress > 1f && !refreshing && isAtTop) {
                    refreshing = true
                }
                
                return Velocity.Zero
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        contentAlignment = Alignment.TopCenter
    ) {
        // Main content
        content()
        
        // Refreshing indicator
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.TopCenter)
                .alpha(progress.coerceIn(0f, 1f)),
            progress = if (refreshing) 1f else progress.coerceIn(0f, 1f),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.5.dp
        )
    }
}
