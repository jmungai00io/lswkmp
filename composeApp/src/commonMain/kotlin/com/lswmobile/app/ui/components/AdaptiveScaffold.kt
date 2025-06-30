package com.lswmobile.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lswmobile.app.ui.navigation.NavigationGraphs
import com.lswmobile.app.ui.navigation.Screen
import com.lswmobile.app.ui.navigation.icon
import com.lswmobile.app.ui.theme.AppTheme
import com.lswmobile.app.ui.utils.WindowSizeClass
import com.lswmobile.app.ui.utils.WindowSizeInfo
import com.lswmobile.app.ui.utils.rememberWindowSizeInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Adaptive scaffold that adjusts its navigation based on screen size
 * - Compact: Bottom navigation bar + modal drawer
 * - Medium: Navigation rail + modal drawer
 * - Expanded: Navigation rail + permanent drawer
 */
@Composable
fun AdaptiveScaffold(
    windowSizeInfo: WindowSizeInfo,
    selectedScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Determine whether to show the drawer
    val drawerItems = NavigationGraphs.drawerItems
    val bottomNavItems = NavigationGraphs.bottomNavItems
    
    // Check if drawer is open to hide navigation rail/bottom bar
    val isDrawerOpen = drawerState.targetValue == DrawerValue.Open
    
    // Different drawer implementations based on screen size
    when {
        // Large screens: Permanent drawer + navigation rail
        windowSizeInfo.shouldShowPermanentDrawer -> {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(240.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Livestock Wealth",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Draw each navigation item
                        drawerItems.forEach { screen ->
                            NavigationDrawerItem(
                                label = { Text(screen.titleRes) },
                                selected = screen.route == selectedScreen.route,
                                onClick = { onScreenSelected(screen) },
                                icon = { 
                                    Icon(
                                        imageVector = screen.icon(screen.route == selectedScreen.route),
                                        contentDescription = screen.titleRes
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Don't show navigation rail with permanent drawer since it's redundant
                    
                    // Main content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        content()
                    }
                }
            }
        }
        
        // Medium screens: Navigation rail + modal drawer
        windowSizeInfo.shouldShowNavigationRail -> {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Livestock Wealth",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Draw each navigation item
                        drawerItems.forEach { screen ->
                            NavigationDrawerItem(
                                label = { Text(screen.titleRes) },
                                selected = screen.route == selectedScreen.route,
                                onClick = { 
                                    onScreenSelected(screen)
                                    scope.launch { drawerState.close() }
                                },
                                icon = { 
                                    Icon(
                                        imageVector = screen.icon(screen.route == selectedScreen.route),
                                        contentDescription = screen.titleRes
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Navigation rail on the side - only show when drawer is closed
                    AnimatedVisibility(
                        visible = !isDrawerOpen,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        NavigationRail(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            bottomNavItems.forEach { screen ->
                                NavigationRailItem(
                                    icon = { 
                                        Icon(
                                            imageVector = screen.icon(screen.route == selectedScreen.route),
                                            contentDescription = screen.titleRes
                                        )
                                    },
                                    label = { Text(screen.titleRes) },
                                    selected = screen.route == selectedScreen.route,
                                    onClick = { onScreenSelected(screen) }
                                )
                            }
                        }
                    }
                    
                    // Main content
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        content()
                    }
                }
            }
        }
        
        // Compact screens: Bottom navigation
        else -> {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Livestock Wealth",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Draw each navigation item
                        drawerItems.forEach { screen ->
                            NavigationDrawerItem(
                                label = { Text(screen.titleRes) },
                                selected = screen.route == selectedScreen.route,
                                onClick = { 
                                    onScreenSelected(screen)
                                    scope.launch { drawerState.close() }
                                },
                                icon = { 
                                    Icon(
                                        imageVector = screen.icon(screen.route == selectedScreen.route),
                                        contentDescription = screen.titleRes
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            ) {
                Scaffold(
                    bottomBar = {
                        // Only show bottom bar if drawer is closed and screen supports bottom bar
                        if (selectedScreen.hasBottomBar && !isDrawerOpen) {
                            AnimatedVisibility(
                                visible = !isDrawerOpen,
                                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.CenterHorizontally),
                                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
                            ) {
                                NavigationBar {
                                    bottomNavItems.forEach { screen ->
                                        NavigationBarItem(
                                            icon = { 
                                                Icon(
                                                    imageVector = screen.icon(screen.route == selectedScreen.route),
                                                    contentDescription = screen.titleRes
                                                )
                                            },
                                            label = { Text(screen.titleRes) },
                                            selected = screen.route == selectedScreen.route,
                                            onClick = { onScreenSelected(screen) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AdaptiveScaffoldPreview() {
    val density = LocalDensity.current
    val width = with(density) { 360.dp.toPx() }
    val height = with(density) { 640.dp.toPx() }
    
    val windowSizeInfo = rememberWindowSizeInfo(
        width = 360.dp,
        height = 640.dp
    )
    
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.MarketPlace) }
    
    AdaptiveScaffold(
        windowSizeInfo = windowSizeInfo,
        selectedScreen = selectedScreen,
        onScreenSelected = { selectedScreen = it }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Content for ${selectedScreen.titleRes}")
        }
    }
}
